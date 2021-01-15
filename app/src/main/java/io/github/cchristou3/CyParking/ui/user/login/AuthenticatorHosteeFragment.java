package io.github.cchristou3.CyParking.ui.user.login;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FragmentAuthenticatorHosteeBinding;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModel;
import io.github.cchristou3.CyParking.ui.host.AuthStateViewModelFactory;
import io.github.cchristou3.CyParking.ui.widgets.DescriptionDialog;
import io.github.cchristou3.CyParking.utilities.ViewUtility;

/**
 * <p>A simple {@link Fragment} subclass.
 * Purpose: Use the {@link AuthenticatorHosteeFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Can be used for both logging in and signing up.</p>
 *
 * @author Charalambos Christou
 * @version 3.0 30/12/20
 */
public class AuthenticatorHosteeFragment extends Fragment implements TextWatcher {

    // Constant variables
    public static final String PAGE_TYPE_KEY = "PAGE_TYPE_KEY";
    private final String TAG = AuthenticatorHosteeFragment.this.getClass().getName() + "UniqueTag";
    // Fragment variables
    private AuthenticatorViewModel mAuthenticatorViewModel;
    private AuthStateViewModel mAuthStateViewModel;
    private FragmentAuthenticatorHosteeBinding mFragmentAuthenticatorHosteeBinding;
    private boolean mIsReauthenticating = false;
    private short mPageType;

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
            this.mPageType = getArguments().getShort(PAGE_TYPE_KEY);
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
        mFragmentAuthenticatorHosteeBinding =
                FragmentAuthenticatorHosteeBinding.inflate(inflater);
        return mFragmentAuthenticatorHosteeBinding.getRoot();
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

        // By passing the parent (AuthenticationFragment)'s ViewModelStoreOwner
        // Both tabs share the same LoginViewModel instance
        mAuthenticatorViewModel = new ViewModelProvider(requireParentFragment(), new AuthenticatorViewModelFactory(
                mAuthStateViewModel.getAuthenticatorRepository() // Both ViewModels use the same instance of AuthenticatorRepository
        ))
                .get(AuthenticatorViewModel.class);

        if (getParentFragment() != null && getParentFragment().getArguments() != null) {
            String email = getParentFragment().getArguments().getString(getString(R.string.email_low));
            Log.d(TAG, "onViewCreated: " + email);
            mAuthenticatorViewModel.updateEmail(email);
            mIsReauthenticating = true;
        }
        initializeFragment();
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
        mAuthenticatorViewModel.isUserSigningIn(!mAuthenticatorViewModel.isUserSigningIn());
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!mAuthenticatorViewModel.isUserSigningIn()) {
            getBinding().fragmentLoginBtnDialogUserButton.setOnClickListener(null);
            getBinding().fragmentLoginBtnDialogOperatorButton.setOnClickListener(null);
            getBinding().fragmentLoginCbRoleUserCheckbox.setOnCheckedChangeListener(null);
            getBinding().fragmentLoginCbRoleOperatorCheckbox.setOnCheckedChangeListener(null);
        } else {
            getBinding().fragmentLoginEtPassword.setOnEditorActionListener(null);
        }
        getBinding().fragmentLoginEtEmail.removeTextChangedListener(this);
        getBinding().fragmentLoginEtPassword.removeTextChangedListener(this);
        mFragmentAuthenticatorHosteeBinding = null;
    }

    /**
     * Sets up the logic of the fragment
     */
    private void initializeFragment() {
        // Get references to the UI elements
        final EditText emailEditText = getBinding().fragmentLoginEtEmail;
        final EditText passwordEditText = getBinding().fragmentLoginEtPassword;

        if (mAuthenticatorViewModel != null) {
            attachObserverToForm();
            attachObserverToResult();

            // Add text listeners to both the user name and the password fields.
            emailEditText.addTextChangedListener(this);
            passwordEditText.addTextChangedListener(this);

            switch (mPageType) {
                case AuthenticatorAdapter.LOGIN_TAB:
                    updateUiForLoggingIn();
                    break;
                case AuthenticatorAdapter.REGISTRATION_TAB:
                    updateUiForRegistration();
                    break;
                default:
                    throw new IllegalStateException("The page type must be one of those:\n"
                            + "LOGIN_PAGE\n"
                            + "REGISTRATION_PAGE");
            }

            // Observe any tab changes
            mAuthenticatorViewModel.getTabState().observe(getViewLifecycleOwner(), state -> {
                // Set the current tab's EditTexts' values with the values of the previous tab
                final String emailText = mAuthenticatorViewModel.getEmailState().getValue();
                final String passwordText = mAuthenticatorViewModel.getPasswordState().getValue();
                emailEditText.setText(emailText);
                passwordEditText.setText(passwordText);
                // Call loginDataChanged to refresh the form's error messages of the current tab,
                // after receiving the inputs from the previous tab.
                notifyDataChanged();

            });
        }
    }

    /**
     * Updates the data of the ViewModel.
     */
    private void notifyDataChanged() {
        mAuthenticatorViewModel.dataChanged(mAuthenticatorViewModel.getEmailState().getValue(),
                mAuthenticatorViewModel.getPasswordState().getValue(),
                (!mAuthenticatorViewModel.isUserSigningIn() &&
                        getBinding().fragmentLoginCbRoleUserCheckbox.isChecked()),
                (!mAuthenticatorViewModel.isUserSigningIn() &&
                        getBinding().fragmentLoginCbRoleOperatorCheckbox.isChecked()));
    }

    /**
     * Prepares the Ui for a user registration attempt.
     */
    private void updateUiForRegistration() {
        // User is registering
        // Set up the UI and listeners for registration
        final CheckBox userCheckbox = getBinding().fragmentLoginCbRoleUserCheckbox;
        final CheckBox operatorCheckbox = getBinding().fragmentLoginCbRoleOperatorCheckbox;

        // create an on checked listener for the checkboxes
        final CompoundButton.OnCheckedChangeListener onCheckedChangeListener =
                (buttonView, isChecked) -> notifyDataChanged();
        // Hook up listeners to both checkboxes
        userCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
        operatorCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);
        // Set up authButton
        setUpAuthButton(R.string.sign_up, v -> register());

        // Add listeners to both "description" buttons
        getBinding().fragmentLoginBtnDialogUserButton.setOnClickListener(
                getRoleDescriptionOnClickListener(
                        R.id.fragment_login_txt_role_user_title, R.string.user_desc));
        getBinding().fragmentLoginBtnDialogOperatorButton.setOnClickListener(
                getRoleDescriptionOnClickListener(
                        R.id.fragment_login_txt_role_operator_title, R.string.op_desc));
    }

    /**
     * Sets the text of the button with the specified one
     * and its on click listener with the given one.
     *
     * @param textId          The text of the authButton.
     * @param onClickListener The OnClickListener of authButton.
     */
    private void setUpAuthButton(@StringRes int textId, View.OnClickListener onClickListener) {
        // Set its text to
        setAuthButtonText(textId);
        // Set its listener to the specified one
        getBinding().fragmentLoginBtnAuthButton.setOnClickListener(onClickListener);
    }

    /**
     * Registers the user.
     */
    private void register() {
        changeVisibilityOfLoadingBarTo(View.VISIBLE);
        ViewUtility.hideKeyboard(requireActivity(), requireView());
        mAuthenticatorViewModel.register(
                getBinding().fragmentLoginEtEmail.getText().toString(),
                getBinding().fragmentLoginEtPassword.getText().toString(),
                getBinding().fragmentLoginCbRoleUserCheckbox.isChecked(),
                getBinding().fragmentLoginCbRoleOperatorCheckbox.isChecked(),
                requireContext());
    }

    /**
     * Show or hide the loading bar based on the specified visibility.
     */
    private void changeVisibilityOfLoadingBarTo(int visibility) {
        if (getBinding().fragmentLoginPbLoading.getVisibility() != visibility)
            getBinding().fragmentLoginPbLoading.setVisibility(visibility);
    }

    /**
     * Prepares the Ui for a user log in attempt.
     * Hides the role area and attaches appropriate listeners.
     */
    private void updateUiForLoggingIn() {
        // Set up the UI and listeners for logging in
        // Hide unnecessary UI elements
        hideRoleArea();
        // Pressing the "enter" on the keyboard will automatically trigger the login method
        getBinding().fragmentLoginEtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mAuthenticatorViewModel.getFormState().getValue().isDataValid())
                    login(requireContext());
            }
            return false;
        });
        setUpAuthButton(R.string.sign_in, v -> login(requireContext()));
    }

    /**
     * Sets the authButton's text to the given one.
     *
     * @param textId The new text of the authButton
     */
    private void setAuthButtonText(int textId) {
        getBinding().fragmentLoginBtnAuthButton.setText(textId);// Set corresponding id string
    }

    /**
     * Logs in the user.
     *
     * @param requiredContext The context of the fragment.
     */
    private void login(Context requiredContext) {
        changeVisibilityOfLoadingBarTo(View.VISIBLE);
        ViewUtility.hideKeyboard(requireActivity(), requireView());
        mAuthenticatorViewModel.login(requiredContext,
                getBinding().fragmentLoginEtEmail.getText().toString(),
                getBinding().fragmentLoginEtPassword.getText().toString());
    }

    /**
     * Looks for changes in the authenticatorResult.
     * Its callback is invoked, whenever the user clicks the authButton (to log in or register).
     */
    private void attachObserverToResult() {
        // Add an observer to the login result state
        mAuthenticatorViewModel.getAuthenticatorResult().observe(getViewLifecycleOwner(), loginResult -> {
            // Also check if the fragment is on its onResume state.
            // In case the user enters his credentials (Both email and password) on the login page
            // and decides to switch the to registration tab (assuming it is the first time the user
            // pressed this tab). Then the registration tab will get initialized that time (it will start from
            // onCreateView till onResume). Thus, as the observer is set on the onViewCreated callback it
            // will trigger immediately with the user's data.
            if (loginResult == null || !this.isResumed()) return;
            changeVisibilityOfLoadingBarTo(View.GONE); // hide the loading bar
            if (loginResult.getError() != null) showLoginFailed(loginResult.getError());
            if (loginResult.getSuccess() != null) {
                if (this.mIsReauthenticating) {
                    mAuthenticatorViewModel.reauthenticateUser(getBinding().fragmentLoginEtPassword.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    updateUiWithUser(loginResult.getSuccess());
                                } else if (task.getException() != null) {
                                    showLoginFailed(task.getException().getMessage());
                                }
                            });
                    return;
                }
                updateUiWithUser(loginResult.getSuccess());
            }
        });
    }

    /**
     * Looks for changes in the form.
     * Its callback is invoked, whenever any of its fields get changed.
     */
    private void attachObserverToForm() {
        // Add an observer to the login form state
        mAuthenticatorViewModel.getFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) return;
            getBinding().fragmentLoginBtnAuthButton
                    .setEnabled(loginFormState.isDataValid());

            // If there is an error, show it for the specific UI field.
            // If there was an error before, and it got resolved then hide the error.
            if (loginFormState.getEmailError() != null) {
                getBinding().fragmentLoginEtEmail.setError(getString(loginFormState.getEmailError()));
            } else {
                if (getBinding().fragmentLoginEtEmail.getError() != null)
                    getBinding().fragmentLoginEtEmail.setError(null, null);
            }
            if (loginFormState.getPasswordError() != null) {
                getBinding().fragmentLoginEtPassword.setError(getString(loginFormState.getPasswordError()));
            } else {
                if (getBinding().fragmentLoginEtPassword.getError() != null)
                    getBinding().fragmentLoginEtPassword.setError(null, null);
            }
            if (!mAuthenticatorViewModel.isUserSigningIn()) {// User signs up
                if (loginFormState.getRoleError() != null) {
                    getBinding().fragmentLoginCbRoleUserCheckbox.setError(getString(loginFormState.getRoleError()));
                    getBinding().fragmentLoginCbRoleOperatorCheckbox.setError(getString(loginFormState.getRoleError()));
                } else {
                    getBinding().fragmentLoginCbRoleUserCheckbox.setError(null, null);
                    getBinding().fragmentLoginCbRoleOperatorCheckbox.setError(null, null);
                }
            }
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
     */
    private void hideRoleArea() {
        getBinding().fragmentLoginClRoleSection.setVisibility(View.GONE);
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

    /**
     * Access the {@link #mFragmentAuthenticatorHosteeBinding}.
     *
     * @return A reference to {@link #mFragmentAuthenticatorHosteeBinding}.
     */
    private FragmentAuthenticatorHosteeBinding getBinding() {
        return mFragmentAuthenticatorHosteeBinding;
    }

    /**
     * This method is called whenever the editText
     * that it is attached to, has its text changed.
     *
     * @param s The updated text.
     */
    @Override
    public void afterTextChanged(Editable s) {
        mAuthenticatorViewModel.dataChanged(
                getBinding().fragmentLoginEtEmail.getText().toString(), // Inputted email
                getBinding().fragmentLoginEtPassword.getText().toString(), // Inputted password
                (!mAuthenticatorViewModel.isUserSigningIn() // If logging in
                        && getBinding().fragmentLoginCbRoleUserCheckbox.isChecked()), // user got checked
                (!mAuthenticatorViewModel.isUserSigningIn() // If logging in
                        && getBinding().fragmentLoginCbRoleOperatorCheckbox.isChecked())); // operator got checked
    }

    /**
     * Unused TextWatcher methods.
     */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* ignore */ }

    public void onTextChanged(CharSequence s, int start, int before, int count) { /* ignore */ }
}