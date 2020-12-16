package io.github.cchristou3.CyParking.view.ui.parking.lots;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.view.data.pojo.parking.lot.SlotOffer;

/**
 * Purpose: <p> Handles how each item of the RecyclerView will look like. </p>
 * Used in {@link RegisterLotFragment} to show the operator-typed users
 * their current Slot Offers.
 *
 * @author Charalambos Christou
 * @version 1.0 14/12/20
 */
public class SlotOfferAdapter extends RecyclerView.Adapter<SlotOfferAdapter.SlotOfferViewHolder> {

    private static View.OnClickListener mOnItemClickListener;
    // Data members
    private final List<SlotOffer> mOfferList;

    /**
     * Public Constructor. Initializes the {@link #mOfferList}
     * with the given argument.
     *
     * @param offerList A reference of the list that holds all the {@link SlotOffer}s
     *                  of a parking lot.
     */
    public SlotOfferAdapter(List<SlotOffer> offerList) {
        this.mOfferList = offerList;
    }

    /**
     * Setter for {@link #mOnItemClickListener}.
     *
     * @param onItemClickListener The {@link View.OnClickListener} object to be
     *                            attached to the {@link SlotOfferViewHolder#mRemovalButton}
     */
    public static void setOnItemClickListener(View.OnClickListener onItemClickListener) {
        SlotOfferAdapter.mOnItemClickListener = onItemClickListener;
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
        final SlotOffer slotOfferInThisPosition = mOfferList.get(position);
        // - replace the contents of the view with that element
        final String duration = Float.toString(slotOfferInThisPosition.getDuration());
        holder.mDuration.setText(duration);
        final String price = Float.toString(slotOfferInThisPosition.getPrice());
        holder.mPrice.setText(price);

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mOfferList.size();
    }

    /**
     * Removes the item in the specified position
     * and notifies the adapter.
     *
     * @param position The index of the item within the list.
     */
    public void remove(int position) {
        mOfferList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Adds the given {@link SlotOffer} instance to the list
     * and notifies the adapter.
     *
     * @param slotOffer A {@link SlotOffer} instance to be added to the list.
     */
    public void insert(SlotOffer slotOffer) {
        mOfferList.add(slotOffer);
        notifyItemInserted(mOfferList.size());
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
        public Button mRemovalButton;

        /**
         * Public Constructor. Gets the necessary references from the UI.
         * Hooks up the {@link #mRemovalButton} with the {@link #mOnItemClickListener} instance
         * and sets the current ViewHolder object as its tag.
         *
         * @param view The UI for a single item.
         */
        public SlotOfferViewHolder(View view) {
            super(view);
            mDuration = view.findViewById(R.id.slot_offer_item_txt_duration);
            mPrice = view.findViewById(R.id.slot_offer_item_txt_price);
            mRemovalButton = view.findViewById(R.id.slot_offer_item__btn_remove);
            mRemovalButton.setTag(this);
            mRemovalButton.setOnClickListener(mOnItemClickListener);
        }
    }
}
