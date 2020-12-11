package io.github.cchristou3.CyParking.view.ui.feedback;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.cchristou3.CyParking.R;

/**
 * Purpose: <p>Allow the user to send feedback to the development team. </p>
 *
 * @author Charalambos Christou
 * @version 1.0 11/12/20
 */
public class FeedbackFragment extends Fragment {

    private FeedbackViewModel mViewModel;

    /**
     * Called to have the fragment instantiate its user interface view.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback_fragment, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Update the UI based on the user's logged in status
        updateUI(view, FirebaseAuth.getInstance().getCurrentUser());

        // Hook up the send feedback button with an onClickListener
        view.findViewById(R.id.feedback_fragment_mbtn_send_feedback).setOnClickListener(v -> {
            // TODO: Store message to Firestore & send notification to administrator
        });

        // Get a reference to the feedback TextView of the UI
        EditText feedbackTextArea = view.findViewById(R.id.feedback_fragment_et_feedback_body);

        // If feedbackTextArea is inside a ScrollView then there is an issue while scrolling TextArea’s inner contents.
        // So, when touching the feedbackTextArea forbid the ScrollView from intercepting touch events.
        feedbackTextArea.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
    }


    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FeedbackViewModel.class);
        // TODO: Use the ViewModel
    }

    /**
     * Updates the UI based on the specified FirebaseUser argument.
     *
     * @param view The user interface view
     * @param user The current FirebaseUser instance if there is one
     */
    public void updateUI(@NonNull View view, @Nullable FirebaseUser user) {
        if (user != null) { // User is logged in
            // Show the name section
            view.findViewById(R.id.feedback_fragment_txt_name_title).setVisibility(View.VISIBLE);
            TextView nameTextView = view.findViewById(R.id.feedback_fragment_txt_name_body);
            nameTextView.setVisibility(View.VISIBLE);

            // Show the email section
            view.findViewById(R.id.feedback_fragment_txt_name_body).setVisibility(View.VISIBLE);
            TextView emailTextView = view.findViewById(R.id.feedback_fragment_txt_email_body);
            emailTextView.setVisibility(View.VISIBLE);

            // Hide the edit text related to the email
            view.findViewById(R.id.feedback_fragment_et_email_body).setVisibility(View.GONE);

            // Display user info
            String userDisplayName = user.getDisplayName();
            if (userDisplayName.equals("") || userDisplayName.equals(null)) {
                userDisplayName = getString(R.string.name_not_found);
            }
            nameTextView.setText(userDisplayName);
            emailTextView.setText(user.getEmail());

        } else {// User is NOT logged in
            // Hide the name details
            view.findViewById(R.id.feedback_fragment_txt_name_title).setVisibility(View.GONE);
            view.findViewById(R.id.feedback_fragment_txt_name_body).setVisibility(View.GONE);

            // Hide the TextView related to the email
            view.findViewById(R.id.feedback_fragment_txt_email_body).setVisibility(View.GONE);

            // Show the edit text related to the email
            view.findViewById(R.id.feedback_fragment_et_email_body).setVisibility(View.VISIBLE);
        }
    }
}