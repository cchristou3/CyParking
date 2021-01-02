package io.github.cchristou3.CyParking.ui.parking.slots.viewBooking;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.parking.slot.booking.Booking;

/**
 * Purpose: <p> Handles how each item of the RecyclerView will look like. </p>
 * Used in {@link ViewBookingsFragment} to show the user's bookings.
 *
 * @author Charalambos Christou
 * @version 1.0 05/11/20
 */
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.MyViewHolder> {

    private static final long DURATION = 500;
    private static View.OnClickListener mOnItemClickListener;
    private final List<Booking> mBookings;
    private boolean mOnAttach = true;

    public BookingAdapter(List<Booking> bookings) {
        this.mBookings = bookings;
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
    public BookingAdapter.MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent,
                                                          int viewType) {
        // create and return a new view
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_item, parent, false));
    }

    /**
     * Replace the contents of a view (invoked by the layout manager).
     *
     * @param holder   The view that will hold the element.
     * @param position The position of the item inside the adapter.
     */
    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
        // - get element of the dataset at this position
        // - replace the contents of the view with that element

        setAnimation(holder.itemView, position);

        final String offer = "Slot offer: " + mBookings.get(position).getBookingDetails().getSlotOffer().toString();
        final CharSequence date = "Date: " + DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                .format(mBookings.get(position).getBookingDetails().getDateOfBooking());
        final String time = "Start time: " + mBookings.get(position).getBookingDetails().getStartingTime();
        final String status = "Status: " + (mBookings.get(position).isCompleted() ? "Completed" : "Pending");
        final String parkingName = "Parking: " + mBookings.get(position).getLotName();

        holder.status.setText(status);
        holder.offer.setText(offer);
        holder.lotName.setText(parkingName);
        holder.date.setText(date);
        holder.time.setText(time);
    }

    /**
     * Return the size of your dataset (invoked by the layout manager).
     *
     * @return The number of elements enlisted in the adapter.
     */
    @Override
    public int getItemCount() {
        return mBookings.size();
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
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView status;
        public TextView offer;
        public TextView lotName;
        public TextView date;
        public TextView time;
        public ImageButton cancelButton;

        public MyViewHolder(View view) {
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
