package io.github.cchristou3.CyParking.ui.components;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.jetbrains.annotations.NotNull;

import io.github.cchristou3.CyParking.R;
import io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking.BookingAdapter;

import static io.github.cchristou3.CyParking.utilities.DrawableUtility.drawableToBitmap;

/**
 * Purpose: Handle item swipe events from a {@link RecyclerView}.
 *
 * @author Charalambos Christou
 * @version 2.0 08/02/21
 */
public class BaseItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    // Constants
    private static final String TAG = BaseItemTouchHelper.class.getCanonicalName();
    private static final int ON_SWIPING_ELEVATION = 20;
    private static final int ON_IDLE_ELEVATION = 2;

    // Data members
    private final Swipeable<Integer> mSwipeable;
    private final Paint mPaint;
    private final Bitmap mBitmap;
    private final int mCardViewId;
    private RectF mBackground;
    private RectF mIconDestination;


    /**
     * Initializes the {@link #mSwipeable}
     * with the given {@link Swipeable<Integer>} object.
     * Also instantiates a {@link Paint} object to be used
     * in {@link #onChildDraw(Canvas, RecyclerView, RecyclerView.ViewHolder, float, float, int, boolean)}.
     * Lastly, generates the {@link Bitmap} to be used when swiping.
     *
     * @param swipeable The handler.
     * @param resources The resources.
     */
    public BaseItemTouchHelper(Swipeable<Integer> swipeable, Resources resources, int cardViewId) {
        super(0, ItemTouchHelper.LEFT);
        this.mSwipeable = swipeable;
        // TODO: 08/02/2021 Get colour as well
        mBitmap = drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.ic_delete, null));
        mPaint = new Paint();
        mCardViewId = cardViewId;
        // Set the color that will be drawn on the canvas
        mPaint.setColor(resources.getColor(R.color.light_red));
    }

    /**
     * Callback used when items are being dragged.
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder   The ViewHolder which is being dragged by the user.
     * @param target       The ViewHolder over which the currently active item is being
     *                     dragged.
     * @return True if the {@code viewHolder} has been moved to the adapter position of
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction  The direction to which the ViewHolder is swiped. It is one of
     *                   {@link ItemTouchHelper#UP}, {@link ItemTouchHelper#DOWN},
     *                   {@link ItemTouchHelper#LEFT} or {@link ItemTouchHelper#RIGHT}. If your
     *                   {@link #getMovementFlags(RecyclerView, RecyclerView.ViewHolder)}
     *                   method
     *                   returned relative flags instead of {@link ItemTouchHelper#LEFT} / {@link ItemTouchHelper#RIGHT};
     *                   `direction` will be relative as well. ({@link ItemTouchHelper#START} or {@link
     *                   ItemTouchHelper#END}).
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Log.d(TAG, "onSwiped: ");
        if (direction == ItemTouchHelper.LEFT) {
            mSwipeable.onSwipeLeft(position);
        }
    }

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     *
     * @param canvas            The canvas which RecyclerView is drawing its children
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was
     *                          interacted and simply animating to its original position
     * @param dX                The amount of horizontal displacement caused by user's action
     * @param dY                The amount of vertical displacement caused by user's action
     * @param actionState       The type of interaction on the View. Is either {@link
     *                          ItemTouchHelper#ACTION_STATE_DRAG} or
     * @param isCurrentlyActive True if this view is currently being controlled by the user or
     *                          false it is simply animating back to its original state.
     * @link ItemTouchHelper#ACTION_STATE_SWIPE}.
     */
    @Override
    public void onChildDraw(
            @NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
            float dX, float dY, int actionState, boolean isCurrentlyActive
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;

            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;
            Log.d(TAG, "onChildDraw: Left: " + itemView.getLeft() + ", Right: " + itemView.getRight()
                    + ", dx: " + dX);
            if (isCurrentlyActive) {
                // Calculate the maximum swipe distance of the left side
                float swipedDistance = -Math.abs(itemView.getLeft() - itemView.getRight());
                if ((dX > swipedDistance && dX < 0)) {
                    // If within the view's range, calculate the RectFs
                    mBackground = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    mIconDestination = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    drawOnCanvas(canvas); // and draw them
                    // from the view's initial position till the new one.
                } else if (dX <= swipedDistance) {
                    // Otherwise, if the user swiped more left that the boundaries
                    // then draw only the view's boundaries, do not draw after the view's
                    // max left position.
                    drawOnCanvas(canvas);
                }
            }

            // Update the item's elevation
            updateItemElevation(itemView, isCurrentlyActive);
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    /**
     * Draws the pre-calculated background
     * and bitmap (icon) {@link RectF}s' on
     * the given canvas.
     *
     * @param canvas the canvas the be drew on.
     */
    private void drawOnCanvas(@NonNull Canvas canvas) {
        canvas.drawRect(mBackground, mPaint);
        canvas.drawBitmap(mBitmap, null, mIconDestination, mPaint);
    }

    /**
     * Update the elevation of the ViewHolder's MaterialCardView
     * based on the given flag.
     *
     * @param itemView          The ViewHolder's root view.
     * @param isCurrentlyActive Indicates whether the view is being controlled by the user.
     */
    private void updateItemElevation(View itemView, boolean isCurrentlyActive) {
        getMaterialCardView(itemView) // Access the card view
                .setCardElevation(
                        isCurrentlyActive ?
                                ON_SWIPING_ELEVATION : // If yes, increase the card's elevation to make it stand out
                                ON_IDLE_ELEVATION // If no, revert the card's elevation back to normal
                );
    }

    /**
     * Get a reference to the view's {@link MaterialCardView} instance.
     *
     * @param itemView The ViewHolder's root view.
     * @return a reference to the view's {@link MaterialCardView} instance.
     */
    private MaterialCardView getMaterialCardView(@NotNull View itemView) {
        return itemView.findViewById(mCardViewId);
    }

    /**
     * Purpose: provide the {@link BookingAdapter} an interface
     * for handling swipe events on its items.
     *
     * @param <T> Any type.
     */
    public interface Swipeable<T> {
        void onSwipeLeft(T object);

        // TODO: 03/02/2021 Add onSwipeRight for further functionality
    }
}
