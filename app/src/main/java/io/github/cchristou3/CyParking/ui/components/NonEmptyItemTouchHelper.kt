package io.github.cchristou3.CyParking.ui.components

import android.content.res.Resources
import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Purpose: Handle item swipe events from a [RecyclerView].
 * Specialization of [BaseItemTouchHelper] that does not allow
 * its items to be reduced down to zero.
 *
 * @author Charalambos Christou
 * @version 2.0 08/02/21
 */
class NonEmptyItemTouchHelper
/**
 * Initializes the [NonEmptyItemTouchHelper].
 *
 * @param swipeable The handler.
 * @param resources The resources.
 */
(swipeable: Swipeable, resources: Resources, cardViewId: Int) : BaseItemTouchHelper(swipeable, resources, cardViewId) {

    private var canSwipe = true

    /**
     * Called when a ViewHolder is swiped by the user.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped. It is one of
     * [ItemTouchHelper.UP], [ItemTouchHelper.DOWN],
     * [ItemTouchHelper.LEFT] or [ItemTouchHelper.RIGHT]. If your
     * [.getMovementFlags]
     * method
     * returned relative flags instead of [ItemTouchHelper.LEFT] / [ItemTouchHelper.RIGHT];
     * `direction` will be relative as well. ([ItemTouchHelper.START] or [ItemTouchHelper.END]).
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (canSwipe) super.onSwiped(viewHolder, direction)
        else
            if (swipeable is Swipeable) {
                swipeable.onNoMoreSwipes()
            }
    }


    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     *
     * @param canvas            The canvas which RecyclerView is drawing its children
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     * interacted and simply animating to its original position
     * @param dX                The amount of horizontal displacement caused by user's action
     * @param dY                The amount of vertical displacement caused by user's action
     * @param actionState       The type of interaction on the View. Is either [ItemTouchHelper.ACTION_STATE_DRAG] or
     *                          [ItemTouchHelper.ACTION_STATE_SWIPE]
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     * false it is simply animating back to its original state.
     * @link ItemTouchHelper#ACTION_STATE_SWIPE}.
     */
    override fun onChildDraw(
            canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        Log.d("onChildDraw2", "onChildDraw: "+recyclerView.adapter?.itemCount+ ", "+viewHolder.itemView.visibility)
        // Do not allow for further swipes if its the last item
        if (!canSwipe(recyclerView)) {
            super.onChildDefaultDraw(canvas, recyclerView, viewHolder, actionState, isCurrentlyActive)
        } else {
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun canSwipe(recyclerView: RecyclerView): Boolean =
            canSwipe.apply { canSwipe = isLastItem(recyclerView) }


    // 1 for header and 1 for the only item in the list
    private fun isLastItem(recyclerView: RecyclerView) = (recyclerView.adapter!!.itemCount > (if (recyclerView.adapter is ConcatAdapter) 2 else 1))


    /**
     * Purpose: Handle events in such the user can no longer swipe items.
     *
     * @param <T> Any type.
     */
    interface Swipeable : BaseItemTouchHelper.Swipeable<Int> {
        fun onNoMoreSwipes()
    }
}