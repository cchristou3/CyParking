package io.github.cchristou3.CyParking.view.ui.login;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.HashSet;
import java.util.Set;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.user.login.LoggedInUserView;
import io.github.cchristou3.CyParking.view.ui.support.DescriptionDialog;

/**
 * <p>A simple {@link Fragment} subclass.
 * Purpose: Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Can be used for both logging in and signing up.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 11/12/20
 */
public class LoginFragment extends Fragment {

    // Constant variables
    public static final String PAGE_TYPE_KEY = "PAGE_TYPE_KEY";
    private final int[] resIdsForRoleArea = new int[]{R.id.fragment_login_txt_roles_header, R.id.fragment_login_txt_role_user_title, R.id.fragment_login_cb_role_user_checkbox, R.id.fragment_login_btn_dialog_user_button, R.id.fragment_login_txt_role_operator_title, R.id.fragment_login_cb_role_operator_checkbox, R.id.fragment_login_btn_dialog_operator_button};
    // Fragment variables
    private LoginViewModel loginViewModel;
    private short pageType;
    private CheckBox userCheckbox;
    private CheckBox operatorCheckbox;

    public LoginFragment() { /* Required empty public constructor */ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pageType The type of the page (LOGIN_PAGE, REGISTRATION_PAGE)
     * @return A new instance of fragment AuthenticationFragment.
     */
    @NotNull
    public static LoginFragment newInstance(short pageType) {
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putShort(PAGE_TYPE_KEY, pageType);
        loginFragment.setArguments(args);
        return loginFragment;
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
     * Invoked at the completion of onCreateView. Initializes fragment's ViewModel.
     * Lastly, it attaches a listener to our UI button
     *
     * @param view               The view of the fragment
     * @param savedInstanceState A bundle which contains info about previously stored data
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            // By passing the parent (AuthenticationFragment)'s ViewModelStoreOwner
            // Both tabs share the same LoginViewModel instance
            loginViewModel = new ViewModelProvider(requireParentFragment(), new LoginViewModelFactory())
                    .get(LoginViewModel.class);
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
        loginViewModel.setUserSigningIn(!loginViewModel.isUserSigningIn());
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

        if (loginViewModel != null) {
            // Add an observer to the login form state
            loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
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
                if (!loginViewModel.isUserSigningIn()) {// User signs up
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
            loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
                if (loginResult == null) return;
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) showLoginFailed(loginResult.getError());
                if (loginResult.getSuccess() != null) updateUiWithUser(loginResult.getSuccess());
            });

            TextWatcher afterTextChangedListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {/* ignore */ }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {/* ignore */}

                @Override
                public void afterTextChanged(Editable s) {
                    loginViewModel.loginDataChanged(emailEditText.getText().toString(),
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
                case AuthenticationAdapter.LOGIN_TAB:
                    // Set up the UI and listeners for logging in
                    // Hide unnecessary UI elements
                    hideRoleArea(view);
                    buttonTextResId = R.string.sign_in; // Set corresponding id string
                    // Pressing the "enter" on the keyboard will automatically trigger the login method
                    passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (loginViewModel.getLoginFormState().getValue().isDataValid())
                                loginViewModel.login(emailEditText.getText().toString(),
                                        passwordEditText.getText().toString());
                        }
                        return false;
                    });
                    onClickListener = v -> { // Set corresponding listener for the login button
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        loginViewModel.login(emailEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    };
                    break;
                case AuthenticationAdapter.REGISTRATION_TAB:
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
                            loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                                    passwordEditText.getText().toString(), userCheckbox.isChecked(), operatorCheckbox.isChecked());
                    userCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
                    operatorCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
                    onClickListener = v -> {  // Set corresponding listener for the sign up button
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        loginViewModel.register(emailEditText.getText().toString(),
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

        loginViewModel.getTabState().observe(getViewLifecycleOwner(), state -> {
            final String emailText = loginViewModel.getEmailState().getValue();
            final String passwordText = loginViewModel.getPasswordState().getValue();
            emailEditText.setText(emailText);
            passwordEditText.setText(passwordText);
            // Call loginDataChanged to refresh the form's error messages of the next tab
            loginViewModel.loginDataChanged(loginViewModel.getEmailState().getValue(),
                    loginViewModel.getPasswordState().getValue(),
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
                int nightModeFlags = this.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                // Instantiate the dialog fragment and show it
                DescriptionDialog dialogFragment = DescriptionDialog.newInstance(roleTitle,
                        getResources().getString(description), nightModeFlags);
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
        for (int resId : resIdsForRoleArea) {
            view.findViewById(resId).setVisibility(View.GONE);
        }
    }


    /**
     * Accesses the local details of the user and updates the UI accordingly.
     *
     * @param model An instance of LoggedInUserView.
     */
    private void updateUiWithUser(@NotNull LoggedInUserView model) {

        // Save the user's roles locally using SharedPreferences
        SharedPreferences.Editor editor = getContext().getApplicationContext()
                .getSharedPreferences("CyParkingPreferences", Context.MODE_PRIVATE)
                .edit();
        Set<String> setOfRoles = new HashSet<>();
        if (model.isUser()) setOfRoles.add("User");
        if (model.isOperator()) setOfRoles.add("Operator");

        editor.putStringSet("roles", setOfRoles);
        editor.apply();

        // TODO: When creating the UI in other fragments, check if the roles are stores locally, if not access them via the cloud Database

        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
            // Go to previous screen
            Navigation.findNavController(requireParentFragment().requireView()).popBackStack();
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