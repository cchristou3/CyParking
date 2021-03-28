package io.github.cchristou3.CyParking.ui.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * <p>A ViewModel implementation.
 * Purpose: Data persistence during configuration changes.</p>
 * Its single attribute is handling toast messages.
 *
 * @author Charalambos Christou
 * @since 2.0 27/03/21
 */
open class ToastViewModel : ViewModel() {

    private val mToastMessage = SingleLiveEvent<Int>()

    /**
     * A LiveData getter of [mToastMessage] to ensure that
     * its value cannot be changed outside of the ViewModel scope.
     * Whereas its setter is private.
     */
    var toastMessage: LiveData<Int> = mToastMessage
        private set

    /**
     * Update the value the [mToastMessage] from the main
     * thread.
     * @param message The new value of [mToastMessage].
     */
    fun updateToastMessage(message: Int) {
        mToastMessage.value = message
    }

    /**
     * Update the value the [mToastMessage] from a background
     * thread.
     * @param message The new value of [mToastMessage].
     */
    fun postToastMessage(message: Int) {
        mToastMessage.postValue(message)
    }
}