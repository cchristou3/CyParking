package io.github.cchristou3.CyParking

import android.content.Context
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import io.github.cchristou3.CyParking.apiClient.remote.repository.StripeRepository

/**
 * Purpose: Ensure the application has only one instance of [Stripe]
 * and [StripeRepository].
 *
 * @see SingletonHolder
 * @constructor Lazily initialize the [Stripe] and [StripeRepository] instances.
 * @author Charalambos Christou
 * @since 1.0 10/03/21
 */
class ServiceLocator private constructor(context: Context) {

    val stripeInstance: Stripe by lazy {
        Stripe(
                context.applicationContext,
                PaymentConfiguration.getInstance(context.applicationContext).publishableKey
        )
    }

    val stripeRepository: StripeRepository by lazy { StripeRepository() }

    // Passing in
    // the ServiceLocator's private constructor (::ServiceLocator)
    companion object : SingletonHolder<ServiceLocator, Context>(::ServiceLocator)

}