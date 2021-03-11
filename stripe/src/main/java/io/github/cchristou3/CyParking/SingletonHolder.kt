package io.github.cchristou3.CyParking

/**
 * Purpose: Allow the subclassing companion object to pass
 * arguments in its Singleton class.
 * It uses a double-checked locking system to ensure thread safety and
 * uses a technique known as lazy initialization to initialize the instance
 * only when first accessed.
 *
 * @see ServiceLocator
 * @see <a href='https://blog.mindorks.com/how-to-create-a-singleton-class-in-kotlin'>Source</a>
 * @constructor Initialize the the [ServiceLocator] instance via its passed constructor.
 * @author Charalambos Christou
 * @since 1.0 10/03/21
 */
open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    /**
     * Access the current instance of [T].
     */
    fun getInstance(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null) {
            return checkInstance
        }

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}