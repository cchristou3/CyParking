package io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.apiClient.model.data.user.LoggedInUser;
import io.github.cchristou3.CyParking.data.interfaces.Navigable;
import io.github.cchristou3.CyParking.databinding.FragmentViewBookingsBinding;
import io.github.cchristou3.CyParking.ui.components.BaseFragment;
import io.github.cchristou3.CyParking.ui.components.BaseItemTouchHelper;
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder;
import io.github.cchristou3.CyParking.ui.views.home.HomeFragment;
import io.github.cchristou3.CyParking.ui.views.host.GlobalStateViewModel;
import io.github.cchristou3.CyParking.ui.views.host.MainHostActivity;
import io.github.cchristou3.CyParking.ui.views.user.account.AccountFragment;
import io.github.cchristou3.CyParking.ui.views.user.feedback.FeedbackFragment;
import io.github.cchristou3.CyParking.ui.views.user.login.AuthenticatorFragment;
import io.github.cchristou3.CyParking.utils.Utility;
import io.github.cchristou3.CyParking.utils.ViewUtility;

/**
 * Purpose: <p>Shows pending / completed bookings of the user / operator?</p>
 * <p>
 * In terms of Authentication, this is achieved by communicating with the hosting
 * activity {@link MainHostActivity} via the {@link GlobalStateViewModel}.
 * </p>
 *
 * @author Charalambos Christou
 * @version 8.0 11/02/21
 */
public class ViewBookingsFragment extends BaseFragment<FragmentViewBookingsBinding>
        implements Navigable, BaseFragment.UserStateUiHandler {

    // Fragment variables
    private static final String TAG = ViewBookingsFragment.class.getCanonicalName() + "UniqueTag";
    private BookingAdapter mBookingAdapter;
    private ViewBookingsViewModel mViewBookingsViewModel;

    /**
     * Initialize the fragment's ViewModels.
     *
     * @param savedInstanceState The previously stored bundle.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewModels();
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @see BaseFragment#onCreateView(ViewBinding)
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(FragmentViewBookingsBinding.inflate(inflater));
    }

    /**
     * Called once, the view has been created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewModelObservers();
    }

    /**
     * Called when the view previously created by {@link #onCreateView} has
     * been detached from the fragment.
     *
     * @see BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        getBinding().fragmentViewBookingsRvRecyclerview.setAdapter(null);
        BookingAdapter.setOnItemClickListener(null);
        super.onDestroyView();
    }

    /**
     * Attaches observers to the user's state and the booking list state.
     */
    private void setViewModelObservers() {
        // Attach an observer to the user's state.
        // If the user logs out while being in this screen, an alert is shown
        observeUserState(this);
        mViewBookingsViewModel.getBookingListState().observe(getViewLifecycleOwner(), bookings -> {
            // The RecyclerView's adapter uses DiffUtil to update the list accordingly
            Log.d(TAG, "observe bookings: ");
            updateUi(bookings);
        });

        mViewBookingsViewModel.getToastMessage().observe(getViewLifecycleOwner(),
                message -> ViewUtility.showToast(requireContext(), message));
    }

    /**
     * Update the Ui based on the given list of bookings.
     *
     * @param bookings A list of bookings.
     */
    private void updateUi(List<Booking> bookings) {

        initializeUi();
        Log.d(TAG, "updateUi: " + bookings);
        if (bookings == null || bookings.isEmpty()) {
            displayMessage();
        } else {
            hideMessage();
            mBookingAdapter.submitList(bookings);
        }
    }

    /**
     * Initializes the fragment's ViewModel.
     */
    private void initializeViewModels() {
        mViewBookingsViewModel =
                new ViewModelProvider(this, new ViewBookingsViewModelFactory())
                        .get(ViewBookingsViewModel.class);
    }

    /**
     * Returns an instance of {@link ItemTouchHelper}.
     * onSwipeLeft: Remove the item from the list and the database
     * and notify the adapter.
     *
     * @return An instance of {@link ItemTouchHelper}.
     */
    @NotNull
    private ItemTouchHelper getItemTouchHelper() {
        return new ItemTouchHelper(new BaseItemTouchHelper(
                itemPosition -> {
                    // Access the current list - with a new reference
                    List<Booking> newBookings = Utility.cloneList(mViewBookingsViewModel.getBookingList());
                    // Remove the booking fro the list and store its id in a variable
                    String bookingToBeCancelledId = newBookings.remove((int) itemPosition).generateDocumentId();
                    // Remove specific booking from the database.
                    mViewBookingsViewModel.cancelParkingBooking(bookingToBeCancelledId);
                    // Update the adapter's list
                    mViewBookingsViewModel.updateBookingList(newBookings);
                }, getResources(), R.id.booking_item_cv // Id of the card view
        ));
    }

    /**
     * Hides any list related Views and displays a message to the user.
     * The message has to do about the user not having any bookings.
     */
    private void displayMessage() {
        updateMessage(true);
    }

    /**
     * Displays any list related Views and hides a message to the user.
     * The message has to do about the user not having any bookings.
     */
    private void hideMessage() {
        updateMessage(false);
    }

    /**
     * Hides/Diaplsy any list related Views and displays/hides a message to the user.
     * based on the given flag.
     */
    private void updateMessage(boolean show) {
        // TODO: 04/02/2021 Update to a more fancier message. e.g. MaterialCardView, icons, etc.
        // Display message to user
        getBinding().fragmentViewBookingsTxtNoBookings.setVisibility(show ? View.VISIBLE : View.GONE);
        // Hide view's related to displaying
        getBinding().fragmentViewBookingsRvRecyclerview.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    /**
     * Initializes the fragment's RecyclerView and its adapter instance.
     */
    private void initializeUi() {
        // Create adapter - set up RecyclerView
        setUpAdapter();
        setUpRecyclerView();
        // Set the RecyclerView's adapter
        getBinding().fragmentViewBookingsRvRecyclerview.setAdapter(mBookingAdapter);
    }

    /**
     * Initialize the RecyclerView's {@link androidx.recyclerview.widget.ListAdapter}.
     * Add an onClick listener to the adapter's items.
     * onClick: Navigate to a screen to view the booking details and its QR code
     * using shared element as animation.
     */
    private void setUpAdapter() {
        if (mBookingAdapter == null)
            mBookingAdapter = new BookingAdapter(new BookingsDiffCallback(), getItemTouchHelper());
        BookingAdapter.setOnItemClickListener(v ->
                getNavController(requireActivity()).navigate(
                        ViewBookingsFragmentDirections.actionNavViewBookingsToNavBookingDetailsFragment(
                                // get the booking that got clicked from the list
                                mBookingAdapter.getCurrentList().get((
                                        // Position of the item that got clicked
                                        (RecyclerView.ViewHolder) v.getTag()).getAdapterPosition())
                        )
                )
        );
    }

    /**
     * Set up RecyclerView with a LayoutManager and set its
     * {@link RecyclerView#hasFixedSize()} flag to true (for optimisations).
     */
    private void setUpRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        getBinding().fragmentViewBookingsRvRecyclerview
                .setLayoutManager(manager);
        getBinding().fragmentViewBookingsRvRecyclerview.setHasFixedSize(true);
        //getBinding().fragmentViewBookingsRvRecyclerview.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AuthenticatorFragment}.
     */
    @Override
    public void toAuthenticator() {
        /* Only logged in users can navigate the current screen (View bookings).
         *  If a user is logged in, the action bar option to "sign in" is hidden.
         *  However, in case the user is in this screen and decides to logout,
         *  an alert will be displayed. */
        getNavController(requireActivity())
                .navigate(
                        ViewBookingsFragmentDirections
                                .actionNavViewBookingsToNavAuthenticatorFragment()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link ViewBookingsFragment}.
     */
    @Override
    public void toBookings() {
        // Already in this screen. Thus, no need to implement it.
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link AccountFragment}.
     */
    @Override
    public void toAccount() {
        getNavController(requireActivity())
                .navigate(
                        ViewBookingsFragmentDirections.actionNavViewBookingsToNavAccount()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link FeedbackFragment}.
     */
    @Override
    public void toFeedback() {
        getNavController(requireActivity())
                .navigate(
                        ViewBookingsFragmentDirections.actionNavViewBookingsToNavFeedback()
                );
    }

    /**
     * Navigates from the current Fragment subclass to the
     * {@link HomeFragment}.
     */
    @Override
    public void toHome() {
        getNavController(requireActivity())
                .navigate(
                        ViewBookingsFragmentDirections.actionNavViewBookingsToNavHome()
                );
    }

    /**
     * Invoked whenever the value of {@link GlobalStateViewModel#getUserState()}
     * gets changed.
     * <strong>Note: if its value has not been set, then it won't get triggered.</strong>
     *
     * @param loggedInUser The new value of {@link GlobalStateViewModel#getUserState()}.
     */
    @Override
    public void onUserStateChanged(@Nullable LoggedInUser loggedInUser) {
        if (loggedInUser == null) { // User has logged out
            AlertBuilder.promptUserToLogIn(getChildFragmentManager(), requireActivity(), this,
                    R.string.logout_view_bookings_screen_msg);
        } else {
            // Get bookings from Firestore
            loadBookings(loggedInUser);
        }
    }

    /**
     * Loads the bookings from the Database.
     *
     * @param loggedInUser The user to fetch the bookings for.
     */
    private void loadBookings(@NotNull LoggedInUser loggedInUser) {
        mViewBookingsViewModel.getUserBookings(loggedInUser.getUserId());
    }
}