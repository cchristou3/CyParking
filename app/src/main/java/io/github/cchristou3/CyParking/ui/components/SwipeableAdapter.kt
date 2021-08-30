package io.github.cchristou3.CyParking.ui.components

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Purpose: A [ListAdapter] that supports swipe events on its items.
 *
 * @author Charalambos Christou
 * @version 1.0 08/02/21
 */
abstract class SwipeableAdapter<T, S : RecyclerView.ViewHolder>(
        mDiffCallback: DiffUtil.ItemCallback<T>,
        private val mItemTouchHelper: ItemTouchHelper
) :
        ListAdapter<T, S>(mDiffCallback) {

    /**
     * Called by RecyclerView when it starts observing this Adapter.
     * Keep in mind that same adapter may be observed by multiple RecyclerViews.
     * Attaches the [mItemTouchHelper] to the RecyclerView.
     *
     * @param recyclerView The RecyclerView instance which started observing this adapter.
     * @see .onDetachedFromRecyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // Attach the item touch helper to the recycler view
        this.mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /**
     * Called by RecyclerView when it stops observing this Adapter.
     * Detaches the [mItemTouchHelper] to the RecyclerView.
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     * @see .onAttachedToRecyclerView
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mItemTouchHelper.attachToRecyclerView(null)
    }
}