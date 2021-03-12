package io.github.cchristou3.CyParking.ui.views.parking.slots.bookingDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Purpose: <p>ViewModel provider factory to instantiate [BookingDetailsViewModel].
 * Required given [BookingDetailsViewModel] has a non-empty constructor</p>
 *
 * @author Charalambos Christou
 * @version 1.0 28/02/21
 */
class BookingDetailsViewModelFactory : ViewModelProvider.Factory {
    /**
     * Creates a new instance of the given `Class`.
     *
     * @param modelClass a `Class` whose instance is requested
     * @param <T>        The type parameter for the ViewModel.
     * @return a newly created ViewModel
     */
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(BookingDetailsViewModel::class.java)) {
            BookingDetailsViewModel(io.github.cchristou3.CyParking.apiClient.remote.repository.DefaultOperatorRepository()) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}