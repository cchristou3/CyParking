package io.github.cchristou3.CyParking.ui.components

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import io.github.cchristou3.CyParking.ui.components.LocationServiceViewModel.Companion.addObserverToLocationServicesError
import io.github.cchristou3.CyParking.ui.helper.AlertBuilder

/**
 * Purpose: <p>Data persistence when configuration changes.
 * Inherited by ViewModels whose fragment has business logic related to
 * retrieving the user's location once.
 * Method [addObserverToLocationServicesError] is useful for handling location
 * failures or unavailability.</p>
 *
 * @author Charalambos Christou
 * @since 1.0 27/03/21
 */
open class LocationServiceViewModel : ViewModel() {

    private val mLocationServicesError = SingleLiveEvent<Any>()

    /**
     * Triggers an event to [mLocationServicesError]'s observers.
     */
    fun postLocationServicesError() {
        mLocationServicesError.postValue(null)
    }

    companion object {

        /**
         * Set an observer to [mLocationServicesError].
         *
         * @param locationServiceViewModel an instance of [LocationServiceViewModel] or any of its subclasses.
         * @param fragment the fragment to make use of.
         */
        @JvmStatic
        fun addObserverToLocationServicesError(
                locationServiceViewModel: LocationServiceViewModel, fragment: Fragment
        ) {
            locationServiceViewModel.mLocationServicesError.observe(fragment.viewLifecycleOwner, {
                // Display a warning to the user - restart app / check settings
                AlertBuilder.showLocationServiceErrorDialog(fragment.requireActivity())
            })
        }
    }
}