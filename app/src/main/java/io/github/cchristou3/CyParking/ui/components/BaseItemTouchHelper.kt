package io.github.cchristou3.CyParking.ui.components

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import io.github.cchristou3.CyParking.R
import io.github.cchristou3.CyParking.apiClient.model.data.parking.lot.SlotOffer
import io.github.cchristou3.CyParking.ui.views.parking.lots.register.SlotOfferAdapter
import kotlin.math.abs

/**
 * Purpose: Handle item swipe events from a [RecyclerView].
 *
 * @author Charalambos Christou
 * @version 3.0 3/08/21
 */
open class BaseItemTouchHelper(// Data members
        private val mSwipeable: Swipeable<Int>, resources: Resources, cardViewId: Int
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    private val mPaint: Paint = Paint()
    private val mBitmap: Bitmap = ResourcesCompat.getDrawable(resources, R.drawable.ic_delete, null)?.toBitmap()!!
    private val mCardViewId: Int = cardViewId
    private lateinit var mBackground: RectF
    private lateinit var mIconDestination: RectF

    val swipeable = mSwipeable

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

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
        if (isViewHolderNotSwipeable(viewHolder)) return
        val position = viewHolder.bindingAdapterPosition
        Log.d(TAG, "onSwiped: ")
        if (direction == ItemTouchHelper.LEFT) {
            mSwipeable.onSwipeLeft(position)
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
     * @param actionState       The type of interaction on the View. Is either [ItemTouchHelper.ACTION_STATE_DRAG]
     * or [ItemTouchHelper.ACTION_STATE_SWIPE]
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     * false it is simply animating back to its original state.
     * @link ItemTouchHelper#ACTION_STATE_SWIPE}.
     */
    override fun onChildDraw(
            canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        Log.d(TAG, "onChildDraw: " + viewHolder.javaClass.canonicalName)
        if (!isViewHolderNotSwipeable(viewHolder)) return

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3
            Log.d(TAG, "onChildDraw: Left: " + itemView.left + ", Right: " + itemView.right
                    + ", dx: " + dX)
            if (isCurrentlyActive) {
                // Calculate the maximum swipe distance of the left side
                val swipedDistance = -abs(itemView.left - itemView.right).toFloat()
                if (dX > swipedDistance && dX < 0) {
                    // If within the view's range, calculate the RectFs
                    mBackground = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                    mIconDestination = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                    drawOnCanvas(canvas) // and draw them
                    // from the view's initial position till the new one.
                } else if (dX <= swipedDistance) {
                    // Otherwise, if the user swiped more left that the boundaries
                    // then draw only the view's boundaries, do not draw after the view's
                    // max left position.
                    drawOnCanvas(canvas)
                }
            }

            // Update the item's elevation
            updateItemElevation(itemView, isCurrentlyActive)
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    fun onChildDefaultDraw(
            canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, 0.0F, 0.0F, actionState, isCurrentlyActive)
    }

    /**
     * Draws the pre-calculated background
     * and bitmap (icon) [RectF]s' on
     * the given canvas.
     *
     * @param canvas the canvas the be drew on.
     */
    protected fun drawOnCanvas(canvas: Canvas) {
        canvas.drawRect(mBackground, mPaint)
        canvas.drawBitmap(mBitmap, null, mIconDestination, mPaint)
    }

    /**
     * Update the elevation of the ViewHolder's MaterialCardView
     * based on the given flag.
     *
     * @param itemView          The ViewHolder's root view.
     * @param isCurrentlyActive Indicates whether the view is being controlled by the user.
     */
    protected fun updateItemElevation(itemView: View, isCurrentlyActive: Boolean) {
        getMaterialCardView(itemView) // Access the card view
                ?.setCardElevation(
                        (if (isCurrentlyActive) ON_SWIPING_ELEVATION else  // If yes, increase the card's elevation to make it stand out
                            ON_IDLE_ELEVATION) // If no, revert the card's elevation back to normal
                                .toFloat())
    }

    /**
     * Get a reference to the view's [MaterialCardView] instance.
     *
     * @param itemView The ViewHolder's root view.
     * @return a reference to the view's [MaterialCardView] instance.
     */
    protected fun getMaterialCardView(itemView: View): MaterialCardView? = itemView.findViewById(mCardViewId)


    private fun isViewHolderNotSwipeable(viewHolder: RecyclerView.ViewHolder) = viewHolder is SlotOfferAdapter.SlotOfferViewHolder


    /**
     * Purpose: provide the [BookingAdapter] an interface
     * for handling swipe events on its items.
     *
     * @param <T> Any type.
    </T> */
    interface Swipeable<T> {
        fun onSwipeLeft(itemPosition: T) // TODO: 03/02/2021 Add onSwipeRight for further functionality
    }

    companion object {
        // Constants
        private val TAG = BaseItemTouchHelper::class.java.canonicalName
        private const val ON_SWIPING_ELEVATION = 20
        private const val ON_IDLE_ELEVATION = 2
    }

    /**
     * Initializes the [.mSwipeable]
     * with the given [<] object.
     * Also instantiates a [Paint] object to be used
     * in [.onChildDraw].
     * Lastly, generates the [Bitmap] to be used when swiping.
     *
     * @param swipeable The handler.
     * @param resources The resources.
     */
    init {
        // Set the color that will be drawn on the canvas
        mPaint.color = resources.getColor(R.color.light_red, null)
    }

}