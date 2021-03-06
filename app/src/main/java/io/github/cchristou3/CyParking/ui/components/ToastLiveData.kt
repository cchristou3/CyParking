package io.github.cchristou3.CyParking.ui.components

import androidx.lifecycle.MutableLiveData

/**
 * Purpose: reset its value to null on [MutableLiveData.onInactive].
 * @author Charalambos Christou
 * @since 1.0 06/03/21
 */
class ToastLiveData : MutableLiveData<Int>() {

    override fun onInactive() {
        super.onInactive()
        value = null // In case configs change / or returning back to the screen
        // it will not show a message.
    }
}