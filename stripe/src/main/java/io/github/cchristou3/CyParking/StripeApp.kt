package io.github.cchristou3.CyParking

import android.app.Application
import com.stripe.android.PaymentConfiguration

/**
 * Purpose: Initialize [PaymentConfiguration].
 *
 * @author Charalambos Christou
 * @since 1.0 10/03/21
 */
open class StripeApp : Application() {

    /**
     * First method to be called when the application loads.
     */
    override fun onCreate() {
        super.onCreate()
        // Initialize the Payment Configuration
        PaymentConfiguration.init(applicationContext,
                BuildConfig.PUBLISHABLE_KEY)
    }
}