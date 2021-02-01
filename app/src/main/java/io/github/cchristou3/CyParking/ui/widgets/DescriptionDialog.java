package io.github.cchristou3.CyParking.ui.widgets;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;

public class DescriptionDialog extends DialogFragment {

    private CharSequence mDescription;
    private CharSequence mTitle;
    private int mNightModeFlags;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DescriptionDialog.
     */
    @NotNull
    public static DescriptionDialog newInstance(CharSequence title, CharSequence description, int nightModeFlags) {
        DescriptionDialog descriptionDialog = new DescriptionDialog();
        descriptionDialog.setDescription(description);
        descriptionDialog.setTitle(title);
        descriptionDialog.mNightModeFlags = nightModeFlags;
        // TODO: 01/02/2021 Update Themes
        //descriptionDialog.setStyle(DialogFragment.STYLE_NORMAL, getStyleConfiguration(nightModeFlags));
        return descriptionDialog;
    }

    /**
     * Returns a style based on the current Night mode configs.
     *
     * @param nightModeFlags Indicates the device's settings on Night mode (On/Off/???)
     * @return TODO: Update
     */
    public static int getStyleConfiguration(int nightModeFlags) {
        //if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) return R.style.CustomDialogDark;

        return R.style.CustomDialog; // UI_MODE_NIGHT_NO or UI_MODE_NIGHT_UNDEFINED
    }

    /**
     * Inflates our Dialog.
     *
     * @param inflater           Inflater which will inflate our view
     * @param container          The parent view
     * @param savedInstanceState A bundle which contains info about previously stored data
     * @return The view of the fragment
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_role_description, container, false);
    }

    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the dialog's title
        requireDialog().setTitle(mTitle);
        // Set the dialog's description
        ((TextView) view.findViewById(R.id.dialog_role_description_txt_description)).setText(mDescription);
        // Hook up the listener to the dismiss button
        view.findViewById(R.id.dialog_role_description_btn_dismiss).setOnClickListener(v -> dismiss());

        if (mNightModeFlags != Configuration.UI_MODE_NIGHT_YES)
            ((Button) view.findViewById(R.id.dialog_role_description_btn_dismiss))
                    .setTextColor(getResources().getColor(R.color.black));
    }

    /**
     * Setters
     */
    public void setDescription(CharSequence mDescription) {
        this.mDescription = mDescription;
    }

    public void setTitle(CharSequence mTitle) {
        this.mTitle = mTitle;
    }
}
