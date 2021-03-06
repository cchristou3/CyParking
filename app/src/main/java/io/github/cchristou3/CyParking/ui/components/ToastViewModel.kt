package io.github.cchristou3.CyParking.ui.components

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

/**
 * <p>A ViewModel implementation.
 * Purpose: Data persistence during configuration changes.</p>
 * Its single attribute is handling toast messages.
 *
 * @author Charalambos Christou
 * @since 1.0 06/03/21
 */
open class ToastViewModel : ViewModel() {

    private val mToastMessage = ToastLiveData()

    /**
     * A LiveData getter of [mToastMessage] to ensure that
     * its value cannot be changed outside of the ViewModel scope.
     * Whereas its setter is private.
     */
    var toastMessage: LiveData<Int> = mToastMessage
        private set

    /**
     * Access the [mToastMessage].
     *
     * @return The state of the [mToastMessage].
     */
    fun updateToastMessage(message: Int) {
        mToastMessage.value = message
    }
}