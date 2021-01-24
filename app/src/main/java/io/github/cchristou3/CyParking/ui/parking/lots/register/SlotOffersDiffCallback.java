package io.github.cchristou3.CyParking.ui.parking.lots.register;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;

/**
 * Purpose: Callback for calculating the diff between two non-null items in a list.
 * Used by {@link SlotOfferAdapter} to handle updates on its items.
 *
 * @author Charalambos Christou
 * @version 1.0 24/01/21
 * @see androidx.recyclerview.widget.DiffUtil.ItemCallback
 */
public class SlotOffersDiffCallback extends DiffUtil.ItemCallback<SlotOffer> {
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
    public boolean areItemsTheSame(@NonNull SlotOffer oldItem, @NonNull SlotOffer newItem) {
        return false;
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
     * This method is called only if {@link #areItemsTheSame(SlotOffer, SlotOffer)} returns {@code true} for
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
    public boolean areContentsTheSame(@NonNull SlotOffer oldItem, @NonNull SlotOffer newItem) {
        return false;
    }
}
