package io.github.cchristou3.CyParking.view.ui.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.MainHostActivity;
import io.github.cchristou3.CyParking.view.ui.support.DescriptionDialog;

/**
 * A simple {@link Fragment} subclass.
 * purpose: Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Can be used for both logging in and signing up.
 *
 * @author Charalambos Christou
 * @version 1.0 1/11/20
 */
public class LoginFragment extends Fragment {

    // Constant variables
    public static String LAYOUT_RED_ID_ARG = "layoutResId";
    public static String IS_LOGIN_FRAGMENT = "isLoginFragment";
    private final int[] resIdsForRoleArea = new int[]{R.id.fragment_login_txt_roles_header, R.id.fragment_login_txt_role_one_title, R.id.fragment_login_cb_role_one_checkbox, R.id.fragment_login_btn_dialog_one_button, R.id.fragment_login_txt_role_two_title, R.id.fragment_login_cb_role_two_checkbox, R.id.fragment_login_btn_dialog_two_button};
    // Fragment variables
    private LoginViewModel loginViewModel;
    private Integer layoutResId;
    private boolean isLoginFragment;
    private EditText usernameEditText;
    private EditText passwordEditText;

    public LoginFragment() { /* Required empty public constructor */ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isLoginFragment true if the fragment is going to be used for login in purposes. False, if used for registration.
     * @param layoutResId     The ResId of the layout.
     * @return A new instance of fragment AuthenticationFragment.
     */
    @NotNull
    public static LoginFragment newInstance(boolean isLoginFragment, @LayoutRes Integer layoutResId, LoginViewModel loginViewModel) {
        LoginFragment loginFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RED_ID_ARG, layoutResId);
        args.putBoolean(IS_LOGIN_FRAGMENT, isLoginFragment);
        loginFragment.setArguments(args);
        loginFragment.setLoginViewModel(loginViewModel);
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
            this.layoutResId = getArguments().getInt(LAYOUT_RED_ID_ARG);
            this.isLoginFragment = getArguments().getBoolean(IS_LOGIN_FRAGMENT);
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
        return inflater.inflate(layoutResId, container, false);
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

        // Get references to the UI elements
        usernameEditText = view.findViewById(R.id.fragment_login_et_email);
        passwordEditText = view.findViewById(R.id.fragment_login_et_password);
        final Button loginButton = view.findViewById(R.id.fragment_login_btn_auth_button);
        final ProgressBar loadingProgressBar = view.findViewById(R.id.fragment_login_pb_loading);
        final CheckBox userCheckbox = view.findViewById(R.id.fragment_login_cb_role_one_checkbox);
        final CheckBox operatorCheckbox = view.findViewById(R.id.fragment_login_cb_role_two_checkbox);

        if (loginViewModel != null) {
            // Add an observer to the login form state
            loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
                if (loginFormState == null) return;
                loginButton.setEnabled(loginFormState.isDataValid());

                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
                if (loginFormState.getRoleError() != null) {
                    userCheckbox.setError(getString(loginFormState.getRoleError()));
                    operatorCheckbox.setError(getString(loginFormState.getRoleError()));
                } else {
                    userCheckbox.setError(null, null);
                    operatorCheckbox.setError(null, null);
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
                    loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), userCheckbox.isChecked(), operatorCheckbox.isChecked());

                }
            };
            // Add text listeners to both the user name and the password fields.
            usernameEditText.addTextChangedListener(afterTextChangedListener);
            passwordEditText.addTextChangedListener(afterTextChangedListener);

            View.OnClickListener onClickListener;
            int buttonTextResId;
            if (!isLoginFragment) { // User is registering
                // Set up the UI and listeners for registration
                buttonTextResId = R.string.sign_up; // Set corresponding id string
                // Add listeners to the checkboxes
                CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) ->
                        loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString(), userCheckbox.isChecked(), operatorCheckbox.isChecked());
                userCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
                operatorCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
                onClickListener = v -> {  // Set corresponding listener for the sign up button
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginViewModel.register(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), userCheckbox.isChecked(), operatorCheckbox.isChecked(), requireContext());
                };
                final Button roleOneDescriptionButton = view.findViewById(R.id.fragment_login_btn_dialog_one_button);
                final Button roleTwoDescriptionButton = view.findViewById(R.id.fragment_login_btn_dialog_two_button);
                // Add listeners to the "description" buttons
                roleOneDescriptionButton.setOnClickListener(getRoleDescriptionOnClickListener(R.id.fragment_login_txt_role_one_title));
                roleTwoDescriptionButton.setOnClickListener(getRoleDescriptionOnClickListener(R.id.fragment_login_txt_role_two_title));
            } else {
                // Set up the UI and listeners for logging in
                // Hide unnecessary UI elements
                hideRoleArea(view);
                buttonTextResId = R.string.sign_in; // Set corresponding id string
                // Pressing the "enter" on the keyboard will automatically trigger the login method
                passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        loginViewModel.login(usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    }
                    return false;
                });
                onClickListener = v -> { // Set corresponding listener for the login button
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                };
            }
            // Set the button's text
            loginButton.setText(buttonTextResId);
            // Set the button's onClick listener
            loginButton.setOnClickListener(onClickListener);
        }

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
     * Callback invoked after onStart has returned.
     * Sets the EditTexts' of this tab with the previous tab's username and password values if there are any.
     */
    @Override
    public void onResume() {
        super.onResume();
        /* For some unknown reason, when the device's night mode changes while the app is running,
         * it causes the loginViewModel to become null. For now we've added two checks, one in onViewCreated callback
         * and another one here which prompts the user to either restart the activity or exit the app.
         * TODO: Find the root of this issue */
        if (loginViewModel == null) {
            new AlertDialog.Builder(getContext()) // create an alert dialog builder
                    .setTitle("Oops!")
                    .setMessage("Something went wrong with the application. Would you like to restart it?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        /* OK. do nothing, just inform the user */
                        requireActivity().finish();
                        startActivity(new Intent(getActivity(), MainHostActivity.class));
                    }).setNegativeButton(android.R.string.no, (dialog, which) -> requireActivity().finish()).show();

        } else {
            final String email = loginViewModel.getEmailState().getValue();
            final String password = loginViewModel.getPasswordState().getValue();
            usernameEditText.setText(email);
            passwordEditText.setText(password);
        }
    }

    /**
     * Callback invoked when we swap tabs.
     * The LiveData objects of the email and password get updated with the current value
     * of their corresponding EditTexts.
     * Further, the user signing in state gets reversed. (From true to false and vice versa)
     */
    @Override
    public void onPause() {
        super.onPause();
        loginViewModel.getEmailState().setValue(usernameEditText.getText().toString());
        loginViewModel.getPasswordState().setValue(passwordEditText.getText().toString());
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

    /**
     * Setter
     */
    public void setLoginViewModel(LoginViewModel loginViewModel) {
        this.loginViewModel = loginViewModel;
    }
}