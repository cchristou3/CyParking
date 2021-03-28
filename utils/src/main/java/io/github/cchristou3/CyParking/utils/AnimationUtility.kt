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
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.Visibility
import io.github.cchristou3.CyParking.utils.poko.OnTransitionEndListener

/**
 * Purpose: <p>Contain all helper / utility methods which the application needs
 * related to the animating Views.</p> The file contains top-level functions that
 * are accessible from everywhere in the application.
 *
 * @author Charalambos Christou
 * @version 3.0 26/03/21
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
 * @param nextAnimation the next animation to perform once this transition has finished.
 */
fun slideBottom(parent: ViewGroup, childToBeAnimated: View, hide: Boolean, duration: Long, nextAnimation: Runnable?) =
        slide(parent, childToBeAnimated, hide, duration, Gravity.BOTTOM, nextAnimation)


/**
 * Perform a vertical sliding animation of the given [childToBeAnimated] view.
 * If [hide] is true then it will slide the above view upwards till it is
 * out of the screen.
 * Otherwise, it will slide the view downwards to its destination starting from
 * the top of the screen.
 *
 * @param parent The parent ViewGroup of the child we want to animate.
 * @param childToBeAnimated The view we want to animate.
 * @param duration The duration of the animation.
 * @param hide Whether to hide or show the animating view.
 * @param nextAnimation the next animation to perform once this transition has finished.
 */
fun slideTop(parent: ViewGroup, childToBeAnimated: View, hide: Boolean, duration: Long, nextAnimation: Runnable?) =
        slide(parent, childToBeAnimated, hide, duration, Gravity.TOP, nextAnimation)


/**
 * Perform a vertical sliding animation of the given [childToBeAnimated] view.
 * If [hide] is true then it will slide the above view towards the given gravity
 * edge ([slideEdge]).
 *
 * @param parent The parent ViewGroup of the child we want to animate.
 * @param childToBeAnimated The view we want to animate.
 * @param duration The duration of the animation.
 * @param hide Whether to hide or show the animating view.
 * @param slideEdge indicates towards which direction to animate towards to.
 * @param nextAnimation the next animation to perform once this transition has finished.
 */
fun slide(
        parent: ViewGroup, childToBeAnimated: View, hide: Boolean,
        duration: Long, @Slide.GravityFlag slideEdge: Int, nextAnimation: Runnable?
) = animate(Slide(slideEdge), parent, childToBeAnimated, hide, duration, nextAnimation)


/**
 * Perform a fading animation of the given [childToBeAnimated] view.
 * If [hide] is true then it will fade out the above view. Otherwise, it
 * will fade it inside the screen.
 *
 * @param parent The parent ViewGroup of the child we want to animate.
 * @param childToBeAnimated The view we want to animate.
 * @param duration The duration of the animation.
 * @param hide Whether to hide or show the animating view.
 * @param mode indicates how to animate the given view.
 * @param nextAnimation the next animation to perform once this transition has finished.
 */
fun fade(
        parent: ViewGroup, childToBeAnimated: View, hide: Boolean,
        duration: Long, mode: Int, nextAnimation: Runnable?
) = animate(Fade(mode), parent, childToBeAnimated, hide, duration, nextAnimation)


/**
 * Perform an animation of the given [childToBeAnimated] view.
 * If [hide] is true then it will animate the above view inside the
 * screen. Otherwise, it will animate it out.
 *
 * @param transition The kind of transition to performed (e.g., [Fade], [Slide]]).
 * @param parent The parent ViewGroup of the child we want to animate.
 * @param childToBeAnimated The view we want to animate.
 * @param duration The duration of the animation.
 * @param hide Whether to hide or show the animating view.
 * @param nextAnimation the next animation to perform once this transition has finished.
 */
fun animate(
        transition: Visibility, parent: ViewGroup, childToBeAnimated: View, hide: Boolean,
        duration: Long, nextAnimation: Runnable?
) {
    transition.duration = duration // how long the animation will last
    // Add the id of the target view that this Transition is interested in
    transition.addTarget(childToBeAnimated.id)
    // Start the next animation (if there is one) once this one finishes
    nextAnimation?.let { transition.addListener(OnTransitionEndListener(nextAnimation)) }

    // The TransitionManager starts the animation and
    // handles updating the scene between the frames
    TransitionManager.beginDelayedTransition(parent, transition)

    // If visibility set to VISIBLE, it will slide towards
    // Otherwise, it will slide backwards.
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