package io.github.cchristou3.CyParking.ui.widgets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import io.github.cchristou3.CyParking.R;

/**
 * Purpose: Display information related to the operator role.
 * <p>
 * <strong>Note:</strong>
 * Could be easily used as a generic Role-display dialog
 * when updated correctly. Useful when future roles are going to
 * be added. E.g. administrator, etc.
 * </p>
 *
 * @author Charalambos Christou
 * @version 2.0 10/02/21
 */
public class DescriptionDialog extends DialogFragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DescriptionDialog.
     */
    @NotNull
    public static DescriptionDialog newInstance() {
        DescriptionDialog descriptionDialog = new DescriptionDialog();
        descriptionDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.Widget_CyParking_Dialog);
        return descriptionDialog;
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
        requireDialog().setTitle(getString(R.string.operator));
        // Set the dialog's description
        ((TextView) view.findViewById(R.id.dialog_role_description_txt_description)).setText(getString(R.string.op_desc));
        // Hook up the listener to the dismiss button
        view.findViewById(R.id.dialog_role_description_btn_dismiss).setOnClickListener(v -> dismiss());

        // Display the role's responsibilities
        ListView listView = view.findViewById(R.id.dialog_role_description_lv);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1,
                Arrays.asList(
                        getString(R.string.op_item1),
                        getString(R.string.op_item2),
                        getString(R.string.op_item3)
                ));
        listView.setAdapter(arrayAdapter);
    }
}
