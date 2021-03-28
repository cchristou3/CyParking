package io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.Booking;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.slot.booking.BookingDetails;
import io.github.cchristou3.CyParking.ui.components.SwipeableAdapter;

/**
 * Purpose: <p> Handles how each item of the RecyclerView will look like. </p>
 * Used in {@link ViewBookingsFragment} to show the user's bookings.
 *
 * @author Charalambos Christou
 * @version 4.0 08/02/21
 */
public class BookingAdapter extends SwipeableAdapter<Booking, BookingAdapter.BookingViewHolder> {

    private static final String TAG = BookingAdapter.class.getName();
    private static final long DURATION = 500;
    private static View.OnClickListener mOnItemClickListener;
    private final RecyclerView.OnScrollListener mOnScrollListener;
    private boolean mOnAttach = true;

    /**
     * Constructor used to initialize the {@link ListAdapter}
     * with a {@link DiffUtil.ItemCallback<Booking>} object.
     *
     * @param diffCallback    The callback to be used to compare the items of both lists.
     * @param itemTouchHelper Enables, swipe events
     */
    protected BookingAdapter(
            @NonNull DiffUtil.ItemCallback<Booking> diffCallback, @NonNull ItemTouchHelper itemTouchHelper
    ) {
        super(diffCallback, itemTouchHelper);
        mOnScrollListener = getOnScrollChangedListener();
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
     * Returns a new instance of {@link RecyclerView.OnScrollListener}.
     *
     * @return An instance of {@link RecyclerView.OnScrollListener}.
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    private RecyclerView.OnScrollListener getOnScrollChangedListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                mOnAttach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        };
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
        final Booking booking = getItem(position);

        final String offer = booking.getBookingDetails().getSlotOffer().toString(holder.itemView.getContext());
        final String date = BookingDetails.getDateText(booking.getBookingDetails().getDateOfBooking());
        final BookingDetails.Time time = booking.getBookingDetails().getStartingTime();
        final String status = Booking.getStatusText(holder.itemView.getContext(), booking.isCompleted());
        final String parkingName = booking.getLotName();

        // - replace the contents of the view with that element
        holder.status.setText(status);
        holder.offer.setText(offer);
        holder.lotName.setText(parkingName);
        holder.date.setText(date);
        holder.time.setText(time.toString());
    }

    /**
     * Called by RecyclerView when it starts observing this Adapter.
     * Attaches the {@link #mOnScrollListener} to the RecyclerView.
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter.
     */
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(mOnScrollListener);
    }

    /**
     * Called by RecyclerView when it stops observing this Adapter.
     * Detaches the recycler view's {@link #mOnScrollListener}.
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     * @see #onAttachedToRecyclerView(RecyclerView)
     */
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.removeOnScrollListener(mOnScrollListener);
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

        public BookingViewHolder(View view) {
            super(view);

            // Hook up the item with an on click listener
            itemView.setOnClickListener(mOnItemClickListener);
            itemView.setTag(this);

            status = view.findViewById(R.id.booking_item_txt_status);
            offer = view.findViewById(R.id.booking_item_txt_offer);
            lotName = view.findViewById(R.id.booking_item_txt_parking_name);
            date = view.findViewById(R.id.booking_item_txt_date);
            time = view.findViewById(R.id.booking_item_txt_time);
        }
    }
}
