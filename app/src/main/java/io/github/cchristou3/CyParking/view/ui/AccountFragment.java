package io.github.cchristou3.CyParking.view.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.ui.support.update.UpdateAccountDialog;

/**
 * Purpose: <p>Going to get replaced with another Navigation option</p>
 *
 * @author Charalambos Christou
 * @version 1.0 29/10/20
 */
public class AccountFragment extends Fragment {

    private UpdateAccountDialog mUpdateAccountDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.fragment_account_mbtn_update_name)
                .setOnClickListener(getButtonListener(R.string.prompt_name, "Updating name", UpdateAccountDialog.UPDATE_DISPLAY_NAME));
        view.findViewById(R.id.fragment_account_mbtn_update_email)
                .setOnClickListener(getButtonListener(R.string.prompt_email, "Updating email", UpdateAccountDialog.UPDATE_EMAIL));
        view.findViewById(R.id.fragment_account_mbtn_update_password)
                .setOnClickListener(getButtonListener(R.string.prompt_password, "Updating password", UpdateAccountDialog.UPDATE_PASSWORD));
        if (FirebaseAuth.getInstance()
                .getCurrentUser() != null) { // If user is logged in
            ((MaterialTextView) view.findViewById(R.id.fragment_account_mtv_display_name)).setText(FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getDisplayName());
            ((MaterialTextView) view.findViewById(R.id.fragment_account_mtv_email)).setText(FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getEmail());
        }

    }

    public View.OnClickListener getButtonListener(@StringRes int field, String filedTitle, short updateState) {
        return v -> {
            FragmentManager fm = isAdded() ? getParentFragmentManager() : null;
            if (fm != null) {
                // Access the device's night mode configurations
                int nightModeFlags = this.requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                mUpdateAccountDialog = UpdateAccountDialog.newInstance(filedTitle,
                        getResources().getString(field),
                        nightModeFlags,
                        updateState);
                mUpdateAccountDialog.show(fm, "updateDialog");
            }
        };
    }
}