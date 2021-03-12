package io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import io.github.cchristou3.CyParking.apiClient.model.parking.slot.booking.Booking;

/**
 * Purpose: Callback for calculating the diff between two non-null items in a list.
 * Used by {@link BookingAdapter} to handle updates on its items.
 *
 * @author Charalambos Christou
 * @version 2.0 04/02/21
 * @see androidx.recyclerview.widget.DiffUtil.ItemCallback
 */
public class BookingsDiffCallback extends DiffUtil.ItemCallback<Booking> {

    /**
     * Called to check whether two objects represent the same item.
     * <p>
     * For example, if your items have unique ids, this method should check their id equality.
     * <p>
     * Note: {@code null} items in the list are assumed to be the same as another {@code null}
     * item and are assumed to not be the same as a non-{@code null} item. This callback will
     * not be invoked for either of those cases.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the two items represent the same object or false if they are different.
     * @see DiffUtil.Callback#areItemsTheSame(int, int)
     */
    @Override
    public boolean areItemsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
        return oldItem.equals(newItem);
    }

    /**
     * Called to check whether two items have the same data.
     * <p>
     * This information is used to detect if the contents of an item have changed.
     * <p>
     * This method to check equality instead of {@link Object#equals(Object)} so that you can
     * change its behavior depending on your UI.
     * <p>
     * For example, if you are using DiffUtil with a
     * {@link RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * <p>
     * This method is called only if {@link DiffUtil.ItemCallback#areItemsTheSame(Object, Object)} returns {@code true} for
     * these items.
     * <p>
     * Note: Two {@code null} items are assumed to represent the same contents. This callback
     * will not be invoked for this case.
     *
     * @param oldItem The item in the old list.
     * @param newItem The item in the new list.
     * @return True if the contents of the items are the same or false if they are different.
     * @see DiffUtil.Callback#areContentsTheSame(int, int)
     */
    @Override
    public boolean areContentsTheSame(@NonNull Booking oldItem, @NonNull Booking newItem) {
        return (oldItem.compareTo(newItem)) == 0;
    }
}
