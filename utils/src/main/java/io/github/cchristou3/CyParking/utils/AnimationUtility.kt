@file:JvmName("AnimationUtility")
@file:JvmMultifileClass

package io.github.cchristou3.CyParking.utilities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.transition.Slide
import androidx.transition.TransitionManager

/**
 * Purpose: <p>Contain all helper / utility methods which the application needs
 * related to the animating Views.</p> The file contains top-level functions that
 * are accessible from everywhere in the application.
 *
 * @author Charalambos Christou
 * @version 2.0 06/03/21
 */

/**
 * Perform a vertical sliding animation of the given [childToBeAnimated] view.
 * If [hide] is true then it will slide the above view downwards till it is
 * out of the screen.
 * Otherwise, it will slide the view upwards to its destination starting from
 * the bottom of the screen.
 *
 * @param parent The parent ViewGroup of the child we want to animate.
 * @param childToBeAnimated The view we want to animate.
 * @param duration The duration of the animation.
 * @param hide Whether to hide or show the animating view.
 */
fun slideVerticallyToBottom(parent: ViewGroup, childToBeAnimated: View, hide: Boolean, duration: Long) {
    // BOTTOM indicates that we want to push the view to the bottom of its container,
    // without changing its size.
    val transition = Slide(Gravity.BOTTOM)
    transition.duration = duration // how long the animation will last
    // Add the id of the target view that this Transition is interested in
    transition.addTarget(childToBeAnimated.id)

    // The TransitionManager starts the animation and
    // handles updating the scene between the frames
    TransitionManager.beginDelayedTransition(parent, transition)

    // If visibility set to VISIBLE, it will slide up
    // Otherwise, it will slide down.
    childToBeAnimated.visibility = if (hide) View.GONE else View.VISIBLE
}

/**
 * Changes the background color of the fragment's "availability" view to either green
 * or red based on availability's change and back to its original background color via
 * animation.
 *
 * @param updatedAvailability The latest availability retrieved from the database.
 * @param oldAvailability     The current availability.
 */
fun animateAvailabilityColorChanges(cardViewParent: CardView, child: View,
                                    updatedAvailability: Int, oldAvailability: Int) {
    if (updatedAvailability != oldAvailability) { // If availability got changed
        animateColorChange(
                cardViewParent,
                child,  // If more spaces, animate with green. Otherwise, animate with red
                if (updatedAvailability > oldAvailability) Color.GREEN else Color.RED)
    }
}

/**
 * Animates the view's color to the specified color
 * and then back to its original one.
 *
 * @param cardViewParent A CardView with children.
 * @param child          The child to have it color animated.
 * @param to             The color to animate.
 */
fun animateColorChange(cardViewParent: CardView, child: View, @ColorInt to: Int) {
    val colorDrawables = arrayOf(ColorDrawable(to), getCardViewColor(cardViewParent))
    val transitionDrawable = TransitionDrawable(colorDrawables)
    child.background = transitionDrawable
    transitionDrawable.startTransition(2000)
}