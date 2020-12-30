package io.github.cchristou3.CyParking.utilities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;

import org.jetbrains.annotations.NotNull;

/**
 * Purpose: <p>Contain all helper / utility methods which the application needs
 * related to the View.</p>
 *
 * @author Charalambos Christou
 * @version 2.0 30/12/20
 */
public class ViewUtility {

    /**
     * Scrolls towards the specified view.
     * Only works, if the parent is an instance of
     * {@link android.widget.ScrollView}.
     *
     * @param view The view to scroll to.
     */
    public static void scrollTo(@NotNull View view) {
        view.getParent().requestChildFocus(view, view);
    }

    /**
     * Animates the view's color to the specified color
     * and then back to its original one.
     *
     * @param view The view to have its color animated.
     * @param to   The color to animate.
     */
    public static void animateColorChange(View view, @ColorInt int to) {
        ColorDrawable[] colorDrawables = {new ColorDrawable(to), getViewColor(view)};
        TransitionDrawable transitionDrawable = new TransitionDrawable(colorDrawables);
        view.setBackground(transitionDrawable);
        transitionDrawable.startTransition(2000);
    }

    /**
     * Access the background color of the given view's root
     * element.
     *
     * @param view The view, to have its parent color returned.
     * @return The ColorDrawable of the view's root.
     */
    public static ColorDrawable getViewColor(@NotNull View view) {
        return (ColorDrawable) view.getRootView().getBackground();
    }

    /**
     * The parent view no longer receives touch events from the specified
     * child view. If a scrollable area (e.g. ListView) is inside a
     * ScrollView then there is an issue while scrolling
     * the scrollable area's inner contents.
     * So, when touching the scrollable area, any touch events are blocked from its parent view.
     *
     * @param view The view that should scroll smoothly without interference.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void disableParentScrollingInterferenceOf(@NotNull View view) {
        view.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
    }

    /**
     * Creates a Bitmap object based on a specified drawable
     *
     * @param drawable the drawable to be placed on a bitmap
     * @return A bitmap used to indicate the user's location
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
