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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.ui.support.DescriptionDialog;

/**
 * <p>A simple {@link Fragment} subclass.
 * Purpose: Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Can be used for both logging in and signing up.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 07/11/20
 */
public class LoginFragment extends Fragment {

    // Constant variables
    public static final String PAGE_TYPE_KEY = "PAGE_TYPE_KEY";
    private final int[] resIdsForRoleArea = new int[]{R.id.fragment_login_txt_roles_header, R.id.fragment_login_txt_role_one_title, R.id.fragment_login_cb_role_one_checkbox, R.id.fragment_login_btn_dialog_one_button, R.id.fragment_login_txt_role_two_title, R.id.fragment_login_cb_role_two_checkbox, R.id.fragment_login_btn_dialog_two_button};
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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // If there is an event send of type LoginViewModel
        // then retrieve it and initialize this fragment's ViewModel
        try {
            loginViewModel = Objects.requireNonNull(EventBus.getDefault().getStickyEvent(LoginViewModel.class));
        } catch (ClassCastException | NullPointerException e) {
            //Log.e(TAG, "onCreateView: ", e);
            // Otherwise, initialize this fragment's ViewModel
            // and send it to the sticky event bucket.
            initializeAndPostViewModel();
        }
        // Result: all fragments which retrieve events of type LoginViewModel
        // share the same LoginViewModel instance.
        // In this case, both Login and Register fragments share the same instance of LoginViewModel.

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    /**
     * Initialize the fragment's LoginViewModel and Post it as an event for future fragments
     * to obtain it.
     */
    public void initializeAndPostViewModel() {
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);
        EventBus.getDefault().postSticky(loginViewModel);
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
        initFragment(view);
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
                if (loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));
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
                        userCheckbox = view.findViewById(R.id.fragment_login_cb_role_one_checkbox);
                        operatorCheckbox = view.findViewById(R.id.fragment_login_cb_role_two_checkbox);
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
                        userCheckbox = view.findViewById(R.id.fragment_login_cb_role_one_checkbox);
                        operatorCheckbox = view.findViewById(R.id.fragment_login_cb_role_two_checkbox);
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
                    final Button roleOneDescriptionButton = view.findViewById(R.id.fragment_login_btn_dialog_one_button);
                    final Button roleTwoDescriptionButton = view.findViewById(R.id.fragment_login_btn_dialog_two_button);
                    // Add listeners to the "description" buttons
                    roleOneDescriptionButton.setOnClickListener(getRoleDescriptionOnClickListener(R.id.fragment_login_txt_role_one_title));
                    roleTwoDescriptionButton.setOnClickListener(getRoleDescriptionOnClickListener(R.id.fragment_login_txt_role_two_title));
                    break;
                default:
                    throw new IllegalStateException("The page type must be one of those:\n" +
                            "LOGIN_PAGE\n" +
                            "REGISTRATION_PAGE");
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
     * @param roleResId The id of a EditText element
     * @return A View.OnClickListener instance
     */
    private View.OnClickListener getRoleDescriptionOnClickListener(@IdRes Integer roleResId) {
        return v -> {
            FragmentManager fm = isAdded() ? getParentFragmentManager() : null;
            if (fm != null) {
                // For the clicked description button, show the text of its corresponding role as the dialog's title.
                CharSequence roleTitle = ((TextView) v.getRootView().findViewById(roleResId)).getText();
                // Access the device's night mode configurations
                int nightModeFlags = this.getContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                DescriptionDialog dialogFragment = DescriptionDialog.newInstance(roleTitle, roleTitle + "....\n.....\n.....", nightModeFlags);
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
        // Traversing all elements of the ui which are associated to the roles and hide each of one of them
        for (int resId : resIdsForRoleArea) {
            view.findViewById(resId).setVisibility(View.GONE);
        }
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

        // TODO: When creating the UI in other fragments, check if the roles are stores locally, if not access them via the Database

        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
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