package io.github.cchristou3.CyParking.ui.views.parking.slots.viewBooking;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

import static io.github.cchristou3.CyParking.utilities.ViewUtility.drawableToBitmap;

/**
 * Purpose: Handle item swipe events from a {@link RecyclerView}.
 *
 * @author Charalambos Christou
 * @version 1.0 03/02/21
 */
public class BookingTouchHelper extends ItemTouchHelper.SimpleCallback {

    // Constants
    private static final String TAG = BookingTouchHelper.class.getCanonicalName();
    private static final int ON_SWIPING_ELEVATION = 20;
    private static final int ON_IDLE_ELEVATION = 2;

    // Data members
    private final Swipeable<Integer> mSwipeable;
    private final Paint paint;
    private final Bitmap mBitmap;

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
    public BookingTouchHelper(Swipeable<Integer> swipeable, Resources resources) {
        super(0, ItemTouchHelper.LEFT);
        this.mSwipeable = swipeable;
        mBitmap = drawableToBitmap(ResourcesCompat.getDrawable(resources, R.drawable.ic_delete, null));
        paint = new Paint();
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

            if (dX > -1000 && dX < 0) {
                paint.setColor(Color.parseColor("#FF6565"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                canvas.drawRect(background, paint);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                canvas.drawBitmap(mBitmap, null, icon_dest, paint);
            }

            // Update the item's elevation
            updateItemElevation(itemView, isCurrentlyActive);
        }
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
        return itemView.findViewById(R.id.booking_item_cv);
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
