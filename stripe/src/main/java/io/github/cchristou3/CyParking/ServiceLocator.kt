package io.github.cchristou3.CyParking

import android.content.Context
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe

/**
 * Purpose: Ensure the application has only one instance of [Stripe].
 *
 * @see SingletonHolder
 * @constructor Initialize the [Stripe] instance.
 * @author Charalambos Christou
 * @since 1.0 10/03/21
 */
class ServiceLocator private constructor(context: Context) {

    var stripeInstance: Stripe = Stripe(
            context.applicationContext,
            PaymentConfiguration.getInstance(context.applicationContext).publishableKey
    )
        private set

    // Passing in
    // the ServiceLocator's private constructor (::ServiceLocator)
    companion object : SingletonHolder<ServiceLocator, Context>(::ServiceLocator)

}