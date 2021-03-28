package io.github.cchristou3.CyParking.utils.poko

import androidx.transition.Transition

/**
 * Purpose: Listener attached to a transition. Once the transition is done, then
 * run the following [Runnable], usually the next animation that needs to be performed
 * after the former one.
 *
 * @author Charalambos Christou
 * @since 1.0 25/03/21
 */
class OnTransitionEndListener(private val nextAnimation: Runnable) : Transition.TransitionListener {

    override fun onTransitionStart(transition: Transition) {
    }

    override fun onTransitionEnd(transition: Transition) {
        nextAnimation.run()
    }

    override fun onTransitionCancel(transition: Transition) {
    }

    override fun onTransitionPause(transition: Transition) {
    }

    override fun onTransitionResume(transition: Transition) {
    }
}