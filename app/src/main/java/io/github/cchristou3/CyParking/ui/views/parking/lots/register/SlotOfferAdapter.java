package io.github.cchristou3.CyParking.ui.views.parking.lots.register;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.data.model.parking.lot.SlotOffer;
import io.github.cchristou3.CyParking.ui.components.SwipeableAdapter;

/**
 * Purpose: <p> Handles how each item of the RecyclerView will look like. </p>
 * Used in {@link RegisterLotFragment} to show the operator-typed users
 * their current Slot Offers.
 *
 * @author Charalambos Christou
 * @version 3.0 09/02/21
 */
public class SlotOfferAdapter extends SwipeableAdapter<SlotOffer, SlotOfferAdapter.SlotOfferViewHolder> {

    /**
     * Constructor used to initialize the {@link ListAdapter}
     * with a {@link DiffUtil.ItemCallback<SlotOffer>} object.
     *
     * @param diffCallback The callback to be used to compare the items of both lists.
     */
    protected SlotOfferAdapter(
            @NonNull DiffUtil.ItemCallback<SlotOffer> diffCallback, @NonNull ItemTouchHelper itemTouchHelper
    ) {
        super(diffCallback, itemTouchHelper);
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
        return new SlotOfferViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slot_offer_item, parent, false));
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
        holder.mDuration.setText(duration);
        final String price = Float.toString(slotOfferInThisPosition.getPrice());
        holder.mPrice.setText(price);
    }

    /**
     * Provide a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    public static class SlotOfferViewHolder extends RecyclerView.ViewHolder {

        // Public data members
        public TextView mDuration;
        public TextView mPrice;

        /**
         * Public Constructor. Gets the necessary references from the UI.
         *
         * @param view The UI for a single item.
         */
        public SlotOfferViewHolder(View view) {
            super(view);
            mDuration = view.findViewById(R.id.slot_offer_item_txt_duration);
            mPrice = view.findViewById(R.id.slot_offer_item_txt_price);
        }
    }
}
