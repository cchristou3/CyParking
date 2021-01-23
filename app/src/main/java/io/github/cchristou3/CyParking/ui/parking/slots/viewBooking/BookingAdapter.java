package io.github.cchristou3.CyParking.ui.parking.slots.viewBooking;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;

/**
 * Purpose: <p> Handles how each item of the RecyclerView will look like. </p>
 * Used in {@link ViewBookingsFragment} to show the user's bookings.
 *
 * @author Charalambos Christou
 * @version 2.0 23/01/21
 */
public class BookingAdapter extends ListAdapter<Booking, BookingAdapter.BookingViewHolder> {

    private static final String TAG = BookingAdapter.class.getName();
    private static final long DURATION = 500;
    private static View.OnClickListener mOnItemClickListener;
    private boolean mOnAttach = true;

    /**
     * Constructor used to initialize the {@link ListAdapter}
     * with a {@link DiffUtil.ItemCallback<Booking>} object.
     *
     * @param diffCallback
     */
    protected BookingAdapter(@NonNull DiffUtil.ItemCallback<Booking> diffCallback) {
        super(diffCallback);
    }

    /**
     * Sets the specified OnClickListener with the BookingAdapter's
     * OnClickListener data member.
     *
     * @param onItemClickListener An instance of OnClickListener.
     */
    public static void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Create new views (invoked by the layout manager)
     * Called when RecyclerView needs a new ViewHolder of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NotNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                                int viewType) {
        // create and return a new view
        return new BookingViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item, parent, false));
    }

    /**
     * Replace the contents of a view (invoked by the layout manager).
     *
     * @param holder   The view that will hold the element.
     * @param position The position of the item inside the adapter.
     */
    @Override
    public void onBindViewHolder(@NotNull BookingViewHolder holder, int position) {
        setAnimation(holder.itemView, position); // Set animator

        // - get element of the booking list at this position
        final String offer = getOfferText(getItem(position).getBookingDetails().getSlotOffer().toString());
        final String date = getDateText(getItem(position).getBookingDetails().getDateOfBooking());
        final String time = getTimeText(getItem(position).getBookingDetails().getStartingTime());
        final String status = getStatusText(getItem(position).isCompleted());
        final String parkingName = getNameText(getItem(position).getLotName());

        // - replace the contents of the view with that element
        holder.status.setText(status);
        holder.offer.setText(offer);
        holder.lotName.setText(parkingName);
        holder.date.setText(date);
        holder.time.setText(time);
    }

    // TODO: 22/01/2021 Replace with getString(...)

    /**
     * Prepares the text of the offer TextView based
     * on the given String.
     *
     * @param offer The String to be appended.
     * @return A String with format `Slot offer: ` + given String.
     */
    @NotNull
    @Contract(pure = true)
    private String getOfferText(String offer) {
        return "Slot offer: " + offer;
    }

    /**
     * Prepares the text of the date TextView based
     * on the given String.
     *
     * @param date The String to be appended.
     * @return A String with format `Date: ` + given String.
     */
    @NotNull
    @Contract(pure = true)
    private String getDateText(Date date) {
        return "Date: " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                .format(date);
    }

    /**
     * Prepares the text of the time TextView based
     * on the given String.
     *
     * @param time The String to be appended.
     * @return A String with format `Start time: ` + given String.
     */
    @NotNull
    @Contract(pure = true)
    private String getTimeText(String time) {
        return "Start time: " + time;
    }

    /**
     * Prepares the text of the status TextView based
     * on the given String.
     *
     * @param status A boolean indicating the status of the booking.
     * @return A String with format `status: ` + given String.
     */
    @NotNull
    @Contract(pure = true)
    private String getStatusText(boolean status) {
        return "Status: " + (status ? "Completed" : "Pending");
    }

    /**
     * Prepares the text of the time TextView based
     * on the given String.
     *
     * @param name The String to be appended.
     * @return A String with format `Parking: ` + given String.
     */
    @NotNull
    @Contract(pure = true)
    private String getNameText(String name) {
        return "Parking: " + name;
    }


    /**
     * Called by RecyclerView when it starts observing this Adapter.
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter.
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                mOnAttach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Creates a fading animation for the new items that appear in the view.
     *
     * @param itemView The view that element occupies.
     * @param i        The elements position.
     */
    private void setAnimation(View itemView, int i) {
        if (!mOnAttach) {
            i = -1;
        }
        boolean isNotFirstItem = i == -1;
        i++;
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", 0.f, 0.5f, 1.0f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animator.setStartDelay(isNotFirstItem ? DURATION / 2 : (i * DURATION));
        animator.setDuration(500);
        animatorSet.play(animator);
        animator.start();
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        public TextView status;
        public TextView offer;
        public TextView lotName;
        public TextView date;
        public TextView time;
        public ImageButton cancelButton;

        public BookingViewHolder(View view) {
            super(view);
            status = view.findViewById(R.id.booking_placeholder_item_txt_status);
            offer = view.findViewById(R.id.booking_placeholder_item_txt_offer);
            lotName = view.findViewById(R.id.booking_placeholder_item_txt_parking_name);
            date = view.findViewById(R.id.booking_placeholder_item_txt_date);
            time = view.findViewById(R.id.booking_placeholder_item_txt_time);
            cancelButton = view.findViewById(R.id.booking_placeholder_item_btn_cancel);
            cancelButton.setTag(this);
            cancelButton.setOnClickListener(mOnItemClickListener);
        }
    }
}
