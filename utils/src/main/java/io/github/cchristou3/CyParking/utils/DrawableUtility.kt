@file:JvmName("DrawableUtility")
@file:JvmMultifileClass

package io.github.cchristou3.CyParking.utilities

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import org.jetbrains.annotations.Contract

/**
 * Purpose: <p>Contain all helper / utility methods which the application needs
 * related to the [Drawable]s.</p> The file contains top-level functions that
 * are accessible from everywhere in the application.
 *
 * @author Charalambos Christou
 * @since 1.0 01/03/21
 */

/**
 * Creates a Bitmap object based on a specified drawable
 *
 * @param drawable the drawable to be placed on a bitmap
 * @return A bitmap used to indicate the user's location
 */
fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    return drawable?.toBitmap()
}

/**
 *  Creates a [Drawable] object and then scales it based on the parent's
 *  width and the given [spaceToTake] float.
 *
 *  @param resources The resources to make use of.
 *  @param parent The parent View.
 *  @param drawableResId The resource id of the drawable to create.
 *  @param spaceToTake a value from [0..1] indicating how much space the drawable
 *  should take from the parent. Where 1 means take all space and 0 means
 *  take none of the space.
 *
 *  @return A [Drawable] object.
 */
fun scaleToMatchParent(resources: Resources, parent: View, drawableResId: Int, spaceToTake: Float): BitmapDrawable? {
    return drawableToBitmap(ResourcesCompat.getDrawable(resources, drawableResId, null))
            ?.scale(
                    // Scale both height and width using the parent's width
                    // so scale it equally from both sides.
                    (parent.measuredWidth * spaceToTake).toInt(),
                    (parent.measuredWidth * spaceToTake).toInt()
            )?.toDrawable(resources)
}

/**
 * Access the background color of the given CardView
 * element.
 *
 * @param cardView The CardView to have its color converted into
 * a ColorDrawable.
 * @return The ColorDrawable of the view's root.
 */
@Contract("_ -> new")
fun getCardViewColor(cardView: CardView): ColorDrawable {
    return ColorDrawable(cardView.cardBackgroundColor.defaultColor)
}

/**
 * Extension method for [Drawable]s.
 * Set color to the given color.
 *
 * @param color colorInt obtained by [Resources.getColor]
 */
fun Drawable.setColor(@ColorInt color: Int) {
    this.colorFilter =    // Source = Drawable, Destination = given color
            // Thus, the drawable shape will remain unchanged
            // and its color will be blended with the given color.
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
}