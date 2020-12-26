package io.github.cchristou3.CyParking.ui.user.login;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.AuthStateViewModel;
import io.github.cchristou3.CyParking.AuthStateViewModelFactory;
import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.pojo.user.LoggedInUser;
import io.github.cchristou3.CyParking.ui.widgets.DescriptionDialog;

/**
 * <p>A simple {@link Fragment} subclass.
 * Purpose: Use the {@link AuthenticatorHosteeFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Can be used for both logging in and signing up.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 11/12/20
 */
public class AuthenticatorHosteeFragment extends Fragment {

    // Constant variables
    public static final String PAGE_TYPE_KEY = "PAGE_TYPE_KEY";
    private final int[] viewIdsForRoleArea = new int[]{R.id.fragment_login_txt_roles_header, R.id.fragment_login_txt_role_user_title, R.id.fragment_login_cb_role_user_checkbox, R.id.fragment_login_btn_dialog_user_button, R.id.fragment_login_txt_role_operator_title, R.id.fragment_login_cb_role_operator_checkbox, R.id.fragment_login_btn_dialog_operator_button};
    // Fragment variables
    private AuthenticatorViewModel authenticatorViewModel;
    private AuthStateViewModel mAuthStateViewModel;

    private short pageType;
    private CheckBox userCheckbox;
    private CheckBox operatorCheckbox;


    public AuthenticatorHosteeFragment() { /* Required empty public constructor */ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pageType The type of the page (LOGIN_PAGE, REGISTRATION_PAGE)
     * @return A new instance of fragment AuthenticatorHosteeFragment.
     */
    @NotNull
    public static AuthenticatorHosteeFragment newInstance(short pageType) {
        AuthenticatorHosteeFragment authenticatorHosteeFragment = new AuthenticatorHosteeFragment();
        Bundle args = new Bundle();
        args.putShort(PAGE_TYPE_KEY, pageType);
        authenticatorHosteeFragment.setArguments(args);
        return authenticatorHosteeFragment;
    }


    /**
     * Initialises the fragment.
     *
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.pageType = getArguments().getShort(PAGE_TYPE_KEY);
        }
    }

    /**
     * Inflates our fragment's view.
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    /**
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModels.
     * Lastly, it attaches a listener to our UI button
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize the AuthStateViewModel instance (key for communicating with MainHostActivity)
        mAuthStateViewModel = new ViewModelProvider(requireActivity(), new AuthStateViewModelFactory())
                .get(AuthStateViewModel.class);
        try {
            // By passing the parent (AuthenticationFragment)'s ViewModelStoreOwner
            // Both tabs share the same LoginViewModel instance
            authenticatorViewModel = new ViewModelProvider(requireParentFragment(), new AuthenticatorViewModelFactory())
                    .get(AuthenticatorViewModel.class);
        } catch (IllegalStateException e) {
            Toast.makeText(getContext(), "Failed to instantiate the fragment's LoginViewModel object", Toast.LENGTH_SHORT).show();
        }
        initFragment(view);
    }


    /**
     * Callback invoked of the current fragment when we swap tabs.
     * The LiveData objects of the email and password get updated with the current value
     * of their corresponding EditTexts.
     * Further, the user signing in state gets reversed. (From true to false and vice versa)
     */
    @Override
    public void onPause() {
        super.onPause();
        // Reverse the ViewModel's isUserSigningIn attribute as we move to the other tab
        authenticatorViewModel.setUserSigningIn(!authenticatorViewModel.isUserSigningIn());
    }

    /**
     * Sets up the logic of the fragment
     *
     * @param view The view of the fragment
     */
    private void initFragment(@NotNull View view) {
        // Get references to the UI elements
        final EditText emailEditText = view.findViewById(R.id.fragment_login_et_email);
        final EditText passwordEditText = view.findViewById(R.id.fragment_login_et_password);
        final Button loginButton = view.findViewById(R.id.fragment_login_btn_auth_button);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.fragment_login_pb_loading);

        if (authenticatorViewModel != null) {
            // Add an observer to the login form state
            authenticatorViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
                if (loginFormState == null) return;
                loginButton.setEnabled(loginFormState.isDataValid());

                // If there is an error, show it for the specific UI field.
                // If there was an error before, and it got resolved then hide the error.
                if (loginFormState.getEmailError() != null) {
                    emailEditText.setError(getString(loginFormState.getEmailError()));
                } else {
                    if (emailEditText.getError() != null)
                        emailEditText.setError(null, null);
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                } else {
                    if (passwordEditText.getError() != null)
                        passwordEditText.setError(null, null);
                }
                if (!authenticatorViewModel.isUserSigningIn()) {// User signs up
                    // Get references to the UI checkboxes if they have not been accessed previously
                    if (userCheckbox == null || operatorCheckbox == null) {
                        userCheckbox = view.findViewById(R.id.fragment_login_cb_role_user_checkbox);
                        operatorCheckbox = view.findViewById(R.id.fragment_login_cb_role_operator_checkbox);
                    }
                    if (loginFormState.getRoleError() != null) {
                        userCheckbox.setError(getString(loginFormState.getRoleError()));
                        operatorCheckbox.setError(getString(loginFormState.getRoleError()));
                    } else {
                        userCheckbox.setError(null, null);
                        operatorCheckbox.setError(null, null);
                    }
                }
            });

            // Add an observer to the login result state
            authenticatorViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
                // Also check if the fragment is on its onResume state.
                // In case the user enters his credentials (Both email and password) on the login page
                // and decides to switch the to registration tab (assuming it is the first time the user
                // pressed this tab). Then the registration tab will get initialized that time (it will start from
                // onCreateView till onResume). Thus, as the observer is set on the onViewCreated callback it
                // will trigger immediately with the user's data
                if (loginResult == null || !this.isResumed()) return;
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) showLoginFailed(loginResult.getError());
                if (loginResult.getSuccess() != null) updateUiWithUser(loginResult.getSuccess());
            });

            // Build a TextWatcher for our two EditTexts (email, password)
            TextWatcher afterTextChangedListener = new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {/* ignore */ }

                public void onTextChanged(CharSequence s, int start, int before, int count) {/* ignore */}

                @Override
                public void afterTextChanged(Editable s) {
                    authenticatorViewModel.loginDataChanged(emailEditText.getText().toString(),
                            passwordEditText.getText().toString(),
                            (userCheckbox != null && userCheckbox.isChecked()),
                            (operatorCheckbox != null && operatorCheckbox.isChecked()));
                }
            };

            // Add text listeners to both the user name and the password fields.
            emailEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);

            View.OnClickListener onClickListener;
            int buttonTextResId;

            switch (pageType) {
                case AuthenticatorAdapter.LOGIN_TAB:
                    // Set up the UI and listeners for logging in
                    // Hide unnecessary UI elements
                    hideRoleArea(view);
                    buttonTextResId = R.string.sign_in; // Set corresponding id string
                    // Pressing the "enter" on the keyboard will automatically trigger the login method
                    passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (authenticatorViewModel.getLoginFormState().getValue().isDataValid())
                                authenticatorViewModel.login(requireContext(), emailEditText.getText().toString(),
                                        passwordEditText.getText().toString());
                        }
                        return false;
                    });
                    onClickListener = v -> { // Set corresponding listener for the login button
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        authenticatorViewModel.login(requireContext(), emailEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    };
                    break;
                case AuthenticatorAdapter.REGISTRATION_TAB:
                    // User is registering
                    // Set up the UI and listeners for registration
                    buttonTextResId = R.string.sign_up; // Set corresponding id string
                    // Get references of the checkboxes from the layout
                    if (userCheckbox == null || operatorCheckbox == null) {
                        userCheckbox = view.findViewById(R.id.fragment_login_cb_role_user_checkbox);
                        operatorCheckbox = view.findViewById(R.id.fragment_login_cb_role_operator_checkbox);
                    }
                    // Add listeners to the checkboxes
                    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) ->
                            authenticatorViewModel.loginDataChanged(emailEditText.getText().toString(),
                                    passwordEditText.getText().toString(), userCheckbox.isChecked(), operatorCheckbox.isChecked());
                    userCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
                    operatorCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
                    onClickListener = v -> {  // Set corresponding listener for the sign up button
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        authenticatorViewModel.register(emailEditText.getText().toString(),
                                passwordEditText.getText().toString(),
                                userCheckbox.isChecked(), operatorCheckbox.isChecked(), requireContext());
                    };
                    final Button roleUserDescriptionButton = view.findViewById(R.id.fragment_login_btn_dialog_user_button);
                    final Button roleOperatorDescriptionButton = view.findViewById(R.id.fragment_login_btn_dialog_operator_button);
                    // Add listeners to the "description" buttons
                    roleUserDescriptionButton
                            .setOnClickListener(
                                    getRoleDescriptionOnClickListener(
                                            R.id.fragment_login_txt_role_user_title, R.string.user_desc));
                    roleOperatorDescriptionButton
                            .setOnClickListener(
                                    getRoleDescriptionOnClickListener(
                                            R.id.fragment_login_txt_role_operator_title, R.string.op_desc));
                    break;
                default:
                    throw new IllegalStateException("The page type must be one of those:\n"
                            + "LOGIN_PAGE\n"
                            + "REGISTRATION_PAGE");
            }
            // Set the button's text
            loginButton.setText(buttonTextResId);
            // Set the button's onClick listener
            loginButton.setOnClickListener(onClickListener);
        }

        // Observe any tab changes
        authenticatorViewModel.getTabState().observe(getViewLifecycleOwner(), state -> {
            // Set the current tab's EditTexts' values with the values of the previous tab
            final String emailText = authenticatorViewModel.getEmailState().getValue();
            final String passwordText = authenticatorViewModel.getPasswordState().getValue();
            emailEditText.setText(emailText);
            passwordEditText.setText(passwordText);
            // Call loginDataChanged to refresh the form's error messages of the current tab,
            // after receiving the inputs from the previous tab.
            authenticatorViewModel.loginDataChanged(authenticatorViewModel.getEmailState().getValue(),
                    authenticatorViewModel.getPasswordState().getValue(),
                    (userCheckbox != null && userCheckbox.isChecked()),
                    (operatorCheckbox != null && operatorCheckbox.isChecked()));
        });
    }

    /**
     * Creates an instance of View.OnClickListener which creates a DialogFragment once triggered.
     *
     * @param roleResId   The id of a EditText element
     * @param description The resource id of a string
     * @return A View.OnClickListener instance
     */
    @NotNull
    @Contract(pure = true)
    private View.OnClickListener getRoleDescriptionOnClickListener(@IdRes Integer roleResId, @StringRes final Integer description) {
        return v -> {
            FragmentManager fm = isAdded() ? getParentFragmentManager() : null;
            if (fm != null) {
                // For the clicked description button, show the text of its corresponding role as the dialog's title.
                CharSequence roleTitle = ((TextView) v.getRootView().findViewById(roleResId)).getText();
                // Access the device's night mode configurations
                int nightModeFlags = this.requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                // Instantiate the dialog fragment and show it
                DescriptionDialog dialogFragment = DescriptionDialog.newInstance(roleTitle,
                        getString(description), nightModeFlags);
                dialogFragment.show(fm, roleTitle + " dialog");
            }
        };
    }

    /**
     * Hides all elements needed for registrations but not for logging in.
     *
     * @param view The view of the fragment.
     */
    private void hideRoleArea(View view) {
        // Traversing all elements of the ui that are associated to the roles and hide each of one of them
        for (int resId : viewIdsForRoleArea) {
            view.findViewById(resId).setVisibility(View.GONE);
        }
    }

    /**
     * Accesses the local details of the user and updates the UI accordingly.
     *
     * @param model An instance of LoggedInUserView.
     */
    private synchronized void updateUiWithUser(@NotNull LoggedInUser model) {
        // Check if the current fragment is onResume state
        // Without this check, this method would get triggered twice, (by each tab) causing unexpected navigation behaviour.
        if (this.isResumed()) {

            // Update the UserState - trigger observer update in MainHostActivity to update the drawer
            mAuthStateViewModel.updateAuthState(model);

            // Display a message to the user
            String welcome = getString(R.string.welcome) + model.getDisplayName();
            if (model.getDisplayName() == null || model.getDisplayName().isEmpty()) {
                welcome = getString(R.string.welcome) + model.getEmail();
            }
            Toast.makeText(requireContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
            // Go to previous screen
            Navigation.findNavController(getParentFragment().requireActivity().findViewById(R.id.fragment_main_host_nv_nav_view))
                    .popBackStack();
        }
    }

    /**
     * Shows an error Toast message to the user.
     *
     * @param errorString The message the user will see.
     */
    private void showLoginFailed(String errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }
}