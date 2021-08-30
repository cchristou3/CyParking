package io.github.cchristou3.CyParking.ui.views.parking.lots.register;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.databinding.SlotOfferItemBinding;
import io.github.cchristou3.CyParking.ui.components.SwipeableAdapter;
import io.github.cchristou3.CyParking.utils.Utility;

/**
 * Purpose: <p> Handles how each item of the RecyclerView will look like. </p>
 * Used in {@link RegisterLotFragment} to show the operator-typed users
 * their current Slot Offers.
 *
 * @author Charalambos Christou
 * @version 4.0 30/08/21
 */
public class SlotOfferAdapter extends SwipeableAdapter<SlotOffer, SlotOfferAdapter.SlotOfferViewHolder> {

    /**
     * Constructor used to initialize the {@link ListAdapter}
     * with a {@link DiffUtil.ItemCallback<SlotOffer>} object.
     *
     * @param diffCallback The callback to be used to compare the items of both lists.
     */
    public SlotOfferAdapter(
            @NonNull DiffUtil.ItemCallback<SlotOffer> diffCallback, @NonNull ItemTouchHelper itemTouchHelper
    ) {
        super(diffCallback, itemTouchHelper);
        setHasStableIds(true);
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent
     * an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public SlotOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create and return a new view
        return new SlotOfferViewHolder(
                SlotOfferItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        );
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link SlotOfferViewHolder#itemView} to reflect the item at the given
     * position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SlotOfferViewHolder holder, int position) {
        // - get element of the offerList at this position
        final SlotOffer slotOfferInThisPosition = getItem(position);
        // - replace the contents of the view with that element
        final String duration = Float.toString(slotOfferInThisPosition.getDuration());
        holder.mBinding.slotOfferItemTxtDuration.setText(duration);
        final String price = Float.toString(slotOfferInThisPosition.getPrice());
        holder.mBinding.slotOfferItemTxtPrice.setText(price);
    }

    /**
     * Return the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        return R.layout.slot_offer_item;
    }

    /**
     * Submits a new list to be diffed, and displayed.
     * <p>
     * If a list is already being displayed, a diff will be computed on a background thread, which
     * will dispatch Adapter.notifyItem events on the main thread.
     *
     * @param list The new list to be displayed.
     */
    @Override
    public void submitList(@Nullable List<SlotOffer> list) {
        super.submitList(Utility.cloneList(list));
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class SlotOfferViewHolder extends RecyclerView.ViewHolder {

        // Public data members
        public SlotOfferItemBinding mBinding;

        /**
         * Public Constructor. Gets the necessary references from the UI.
         *
         * @param binding The UI for a single item.
         */
        public SlotOfferViewHolder(@NotNull SlotOfferItemBinding binding) {
            super(binding.getRoot().getRootView());
            mBinding = binding;
        }

        @Override
        public String toString() {
            return mBinding.slotOfferItemTxtPrice.getText() + " & " +mBinding.slotOfferItemTxtDuration.getText();
        }
    }
}
