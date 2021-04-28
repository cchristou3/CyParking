package io.github.cchristou3.CyParking.ui.views.user.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.databinding.FragmentAuthenticatorHosteeBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.widgets.DescriptionDialog;
import io.github.cchristou3.CyParking.utils.ViewUtility;

import static io.github.cchristou3.CyParking.utils.ViewUtility.getStringOrEmpty;

/**
 * <p>A simple {@link Fragment} subclass.
 * Purpose: Use the {@link AuthenticatorHosteeFragment#newInstance} factory method to
 * create an instance of this fragment.
 * Can be used for both logging in and signing up.</p>
 * <p>
 *
 * @author Charalambos Christou
 * @version 10.0 28/04/21
 */
public class AuthenticatorHosteeFragment extends BaseFragment<FragmentAuthenticatorHosteeBinding> implements TextWatcher {

    // Constant variables
    public static final String PAGE_TYPE_KEY = "PAGE_TYPE_KEY";
    private final String TAG = AuthenticatorHosteeFragment.this.getClass().getName() + "UniqueTag";
    private static final int INITIAL_AUTH_BUTTON_TOP_MARGIN = 79;
    private static final int LOGGING_IN_AUTH_BUTTON_TOP_MARGIN = 79 + 60;
    // Fragment variables
    private AuthenticatorViewModel mAuthenticatorViewModel;
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
     * @see BaseFragment#onCreateView(ViewBinding, int)
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(FragmentAuthenticatorHosteeBinding.inflate(inflater), R.string.app_name);
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
        // By passing the parent (AuthenticationFragment)'s ViewModelStoreOwner
        // Both tabs share the same LoginViewModel instance
        mAuthenticatorViewModel = new ViewModelProvider(requireParentFragment(), new AuthenticatorViewModelFactory(
                getGlobalStateViewModel().getAuthenticatorRepository() // Both ViewModels use the same instance of AuthenticatorRepository
        ))
                .get(AuthenticatorViewModel.class);

        checkIfUserReAuthenticating();

        addObserverToForm();
        addObserverToResult();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + mAuthenticatorViewModel.getEmailState().getValue());
        initializeFragment();
        mAuthenticatorViewModel.isUserSigningIn(mPageType == AuthenticatorAdapter.LOGIN_TAB);
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
        cleanUpListeners();
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment. Cleans up all listeners.
     *
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Remove all set listeners.
     */
    private void cleanUpListeners() {
        if (!mAuthenticatorViewModel.isUserSigningIn()) {
            super.removeOnClickListeners(
                    getBinding().fragmentHosteeAuthBtnDialogOperatorButton,
                    getBinding().fragmentHosteeAuthCbRoleOperatorCheckbox,
                    getBinding().fragmentHosteeAuthEtName
            );
        } else {
            getBinding().fragmentHosteeAuthEtPassword.setOnEditorActionListener(null);
            getBinding().fragmentHosteeAuthTxtForgotPassword.setOnClickListener(null);
        }
        super.removeTextWatchers(
                this,
                getBinding().fragmentHosteeAuthEtEmail,
                getBinding().fragmentHosteeAuthEtPassword
        );
    }

    /**
     * Access the text of the given state that was inputted on the previous tab.
     *
     * @param state  the state that contains info about the user's input.
     * @param action how to deal with the input.
     */
    private void getTextFromPreviousTab(@NotNull LiveData<String> state, Consumer<String> action) {
        if (state.getValue() == null) return;
        action.accept(state.getValue());
    }

    /**
     * Access the parent's arguments. If an email was found
     * then the user is re-authenticating.
     * Sets the appropriate EditText's text to the email.
     */
    public void checkIfUserReAuthenticating() {
        if (getParentFragment() != null && getParentFragment().getArguments() != null) {
            String email = getParentFragment().getArguments().getString(getString(R.string.email_low));
            if (email == null || email.isEmpty()) return;
            Log.d(TAG, "checkIfUserReAuthenticating: " + email);
            mAuthenticatorViewModel.updateEmail(email);
            mIsReauthenticating = true;
        }
    }

    /**
     * Sets up the logic of the fragment
     */
    private void initializeFragment() {
        // Get references to the UI elements
        final EditText emailEditText = getBinding().fragmentHosteeAuthEtEmail;
        final EditText passwordEditText = getBinding().fragmentHosteeAuthEtPassword;

        getTextFromPreviousTab(mAuthenticatorViewModel.getEmailState(), emailEditText::setText);
        getTextFromPreviousTab(mAuthenticatorViewModel.getPasswordState(), passwordEditText::setText);

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

    }

    /**
     * Updates the data of the ViewModel.
     */
    private void notifyDataChanged() {
        mAuthenticatorViewModel.dataChanged(
                getStringOrEmpty(getBinding().fragmentHosteeAuthEtEmail), // Inputted email
                getStringOrEmpty(getBinding().fragmentHosteeAuthEtName), // Inputted name
                getStringOrEmpty(getBinding().fragmentHosteeAuthEtPassword) // Inputted password
        ); // operator got checked
    }

    /**
     * Prepares the Ui for a user registration attempt.
     */
    private void updateUiForRegistration() {
        // Add a text watcher to the name edit text
        getBinding().fragmentHosteeAuthEtName.addTextChangedListener(this);

        // Hide the 'forgot password?' text
        getBinding().fragmentHosteeAuthTxtForgotPassword.setVisibility(View.GONE);

        getCheckBox().setOnCheckedChangeListener( // Set an on checked changed listener to the checkbox
                (buttonView, isChecked) -> mAuthenticatorViewModel
                        .updateIsOperatorChecked(getCheckBox().isChecked()) // Update the livedata's value
        );

        // Set up authButton
        setUpAuthButton(R.string.sign_up, this::register);

        // Add listeners to both "description" buttons
        getBinding().fragmentHosteeAuthBtnDialogOperatorButton.setOnClickListener(
                getRoleDescriptionOnClickListener(
                ));
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
        getBinding().fragmentHosteeAuthBtnAuthButton.setOnClickListener(onClickListener);
    }

    /**
     * Registers the user.
     */
    private void register(View view) {
        getGlobalStateViewModel().showLoadingBar();
        ViewUtility.hideKeyboard(requireActivity(), view.getRootView());
        mAuthenticatorViewModel.register(requireContext());
    }

    /**
     * Prepares the Ui for a user log in attempt.
     * Hides the role area and attaches appropriate listeners.
     */
    private void updateUiForLoggingIn() {
        // Set up the UI and listeners for logging in
        // Hide the password hint
        getBinding().fragmentHosteeAuthTilPassword.setHelperText(null);

        // Display the 'forgot password?' message
        getBinding().fragmentHosteeAuthTxtForgotPassword.setVisibility(View.VISIBLE);
        getBinding().fragmentHosteeAuthTxtForgotPassword.setOnClickListener(
                v -> mAuthenticatorViewModel.sendPasswordResetLink(requireContext(), getStringOrEmpty(getBinding().fragmentHosteeAuthEtEmail),
                        getGlobalStateViewModel()::updateToastMessage));

        // It should change only once
        int currentAuthButtonTopMargin = ((ConstraintLayout.LayoutParams) getBinding().fragmentHosteeAuthBtnAuthButton.getLayoutParams()).topMargin;
        if (currentAuthButtonTopMargin == INITIAL_AUTH_BUTTON_TOP_MARGIN) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) getBinding().fragmentHosteeAuthBtnAuthButton.getLayoutParams();
            params.topMargin = LOGGING_IN_AUTH_BUTTON_TOP_MARGIN;
            getBinding().fragmentHosteeAuthBtnAuthButton.setLayoutParams(params);
        }

        // Hide unnecessary UI elements, related to registration
        hideUnrelatedFields();
        // Pressing the "enter" on the keyboard will automatically trigger the login method
        getBinding().fragmentHosteeAuthEtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (mAuthenticatorViewModel.isFormValid())
                    login(v);
            }
            return false;
        });
        setUpAuthButton(R.string.action_sign_in, this::login);
    }

    /**
     * Sets the authButton's text to the given one.
     *
     * @param textId The new text of the authButton
     */
    private void setAuthButtonText(int textId) {
        getBinding().fragmentHosteeAuthBtnAuthButton.setText(textId);// Set corresponding id string
    }

    /**
     * Logs in the user.
     */
    private void login(View view) {
        getGlobalStateViewModel().showLoadingBar();
        ViewUtility.hideKeyboard(requireActivity(), view);
        mAuthenticatorViewModel.login(requireContext(),
                getStringOrEmpty(getBinding().fragmentHosteeAuthEtEmail),
                getStringOrEmpty(getBinding().fragmentHosteeAuthEtPassword));
    }

    /**
     * Looks for changes in the authenticatorResult.
     * Its callback is invoked, whenever the user clicks the authButton (to log in or register).
     */
    private void addObserverToResult() {
        // Add an observer to the login result state
        mAuthenticatorViewModel.getAuthenticatorResult().observe(getViewLifecycleOwner(), authResult -> {
            // Also check if the fragment is on its onResume state.
            // In case the user enters his credentials (Both email and password) on the login page
            // and decides to switch the to registration tab (assuming it is the first time the user
            // pressed this tab). Then the registration tab will get initialized that time (it will start from
            // onCreateView till onResume). Thus, as the observer is set on the onViewCreated callback it
            // will trigger immediately with the user's data.
            if (authResult == null || !this.isResumed()) return;
            if (authResult.getError() != null) showLoginFailed(authResult.getError());
            if (authResult.getSuccess() != null) {
                if (this.mIsReauthenticating) {
                    mAuthenticatorViewModel.reauthenticateUser(getStringOrEmpty(getBinding().fragmentHosteeAuthEtPassword))
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    updateUiWithUser(authResult.getSuccess());
                                } else if (task.getException() != null) {
                                    showLoginFailed(task.getException().getMessage());
                                }
                            });
                    return;
                }
                updateUiWithUser(authResult.getSuccess());
            }
        });
    }

    /**
     * Looks for changes in the form.
     * Its callback is invoked, whenever any of its fields get changed.
     */
    private void addObserverToForm() {
        // Add an observer to the login form state
        mAuthenticatorViewModel.getFormState().observe(getViewLifecycleOwner(), formState -> {
            if (formState == null) return;
            getBinding().fragmentHosteeAuthBtnAuthButton // Dis/enable button based on the form's validity
                    .setEnabled(formState.isDataValid());

            // If there is an error, show it for the specific UI field.
            // If there was an error before, and it got resolved then hide the error.
            ViewUtility.updateErrorOf(requireContext(), getBinding().fragmentHosteeAuthTilEmail, formState.getEmailError());
            ViewUtility.updateErrorOf(requireContext(), getBinding().fragmentHosteeAuthTilPassword, formState.getPasswordError());

            if (!mAuthenticatorViewModel.isUserSigningIn()) {// User signs up
                ViewUtility.updateErrorOf(requireContext(), getBinding().fragmentHosteeAuthTilName, formState.getNameError());
            }
        });
    }


    /**
     * Creates an instance of View.OnClickListener which creates a DialogFragment once triggered.
     *
     * @return A View.OnClickListener instance
     */
    @NotNull
    @Contract(pure = true)
    private View.OnClickListener getRoleDescriptionOnClickListener() {
        return v -> {
            FragmentManager fm = isAdded() ? getParentFragmentManager() : null;
            if (fm != null) {
                // Instantiate the dialog fragment and show it
                DescriptionDialog dialogFragment = DescriptionDialog.newInstance();
                dialogFragment.show(fm, DescriptionDialog.class.getCanonicalName());
            }
        };
    }

    /**
     * Hides all elements needed for registrations but not for logging in.
     */
    private void hideUnrelatedFields() {
        getBinding().fragmentHosteeAuthClRoleSection.setVisibility(View.GONE);
        getBinding().fragmentHosteeAuthTilName.setVisibility(View.GONE);
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
            // Hide the loading bar
            getGlobalStateViewModel().hideLoadingBar();

            // Update the UserState - trigger observer update in MainHostActivity to update the drawer
            getGlobalStateViewModel().updateAuthState(model);

            // Display a message to the user
            String welcome = getString(R.string.welcome) + model.getDisplayName();
            if (model.getDisplayName() == null || model.getDisplayName().isEmpty()) {
                welcome = getString(R.string.welcome) + model.getEmail();
            }
            Toast.makeText(requireContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
            // Go to previous screen
            try {
                ((AuthenticatorFragment) getParentFragment())
                        .goBack(getParentFragment().requireActivity());
            } catch (ClassCastException | NullPointerException e) {
                Log.d(TAG, "updateUiWithUser - error transitioning back: " + e);
                requireActivity().onBackPressed();
            }
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
            getGlobalStateViewModel().hideLoadingBar();
        }
    }

    /**
     * Access the fragment's checkbox.
     *
     * @return A reference to the fragment's only {@link CheckBox} widget.
     */
    private CheckBox getCheckBox() {
        return getBinding().fragmentHosteeAuthCbRoleOperatorCheckbox;
    }

    /**
     * This method is called whenever the editText
     * that it is attached to, has its text changed.
     *
     * @param s The updated text.
     */
    @Override
    public void afterTextChanged(Editable s) {
        notifyDataChanged();
    }

    /**
     * Unused TextWatcher methods.
     */
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* ignore */ }

    public void onTextChanged(CharSequence s, int start, int before, int count) { /* ignore */ }
}