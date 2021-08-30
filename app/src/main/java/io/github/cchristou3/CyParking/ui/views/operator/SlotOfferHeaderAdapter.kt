package io.github.cchristou3.CyParking.ui.views.operator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.databinding.SlotOfferItemHeaderBinding
import io.github.cchristou3.CyParking.ui.helper.DropDownMenuHelper.Companion.setUpSpinner
import io.github.cchristou3.CyParking.utils.checkAndSetOnClickListener

/**
 * An adapter class to be shown as a the header of a list of items via a [ConcatAdapter].
 *
 * @author Charalambos Christou
 * @version 1.0 30/08/21
 */
class SlotOfferHeaderAdapter(
        onDurationSelected: OnItemSelected, onPriceSelected: OnItemSelected, onAddButtonClicked: View.OnClickListener)
    : RecyclerView.Adapter<SlotOfferHeaderAdapter.SlotOfferHeaderViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val onAdd by lazy { onAddButtonClicked }
    private val mDuration by lazy { onDurationSelected }
    private val mPrice by lazy { onPriceSelected }


    fun interface OnItemSelected {
        fun onItemSelected(value: Float?): Unit
    }

    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotOfferHeaderViewHolder {
        return SlotOfferHeaderViewHolder(SlotOfferItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SlotOfferHeaderViewHolder, position: Int) {
        // Set up both spinners
        setUpSpinner(holder.mBinding.slotOfferItemHeaderFragmentSDuration,
                { selectedDuration: Float? -> mDuration.onItemSelected(selectedDuration) },
                1.0f)
        // Minimum charge amount: 0.50 cents in euros: https://stripe.com/docs/currencies#minimum-and-maximum-charge-amounts
        setUpSpinner(holder.mBinding.slotOfferItemHeaderFragmentSPrice,
                { selectedPrice: Float? -> mPrice.onItemSelected(selectedPrice) },
                0.5f)

        holder.mBinding.slotOfferItemHeaderFragmentBtnAdd.checkAndSetOnClickListener(onAdd)
    }

    /**
     * TODO: Investigate bug and fully test slot offer fragment and interactions,
     *  also add add button implementation
     */
    override fun onBindViewHolder(holder: SlotOfferHeaderViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNullOrEmpty()){
            // Full update
            onBindViewHolder(holder, position)
        } else {
            // // Partial update
            val isEnabled = payloads[0]
            if (isEnabled is Boolean){
                holder.mBinding.slotOfferItemHeaderFragmentBtnAdd.isEnabled = isEnabled
            }
        }
    }

    /**
     * Return the view type of the item at `position` for the purposes
     * of view recycling.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * `position`. Type codes need not be contiguous.
     */
    override fun getItemViewType(position: Int): Int = R.layout.slot_offer_item_header


    // total number of rows
    override fun getItemCount(): Int = 1

    // stores and recycles views as they are scrolled off screen
    inner class SlotOfferHeaderViewHolder internal constructor(binding: SlotOfferItemHeaderBinding)
        : RecyclerView.ViewHolder(binding.root.rootView) {

        val mBinding by lazy { binding }
    }
}