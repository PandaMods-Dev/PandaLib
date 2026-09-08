package dev.pandasystems.pandalib.event

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Event<T> {
    fun subscribe(listener: (context: T) -> Unit): Subscription
    operator fun invoke(context: T): T
}

fun interface Subscription {
    fun unsubscribe()
}

fun <T> platformEvent(
    onSubscribe: (listener: (context: T) -> Unit) -> Subscription,
    onInvoke: (context: T) -> T
): Event<T> = object : Event<T> {
    override fun subscribe(listener: (context: T) -> Unit): Subscription {
        val subscription = onSubscribe(listener)
        return Subscription {
            subscription.unsubscribe()
        }
    }

    override fun invoke(context: T): T {
        return onInvoke(context)
    }
}

fun <T> event(): Event<T> {
    val listeners = CopyOnWriteArrayList<(context: T) -> Unit>()

    return object : Event<T> {
        override fun subscribe(listener: (context: T) -> Unit): Subscription {
            listeners += listener
            val subscribed = AtomicBoolean(true)
            return Subscription {
                if (subscribed.compareAndSet(true, false)) {
                    listeners -= listener
                }
            }
        }

        override fun invoke(context: T): T {
            listeners.forEach { it(context) }
            return context
        }
    }
}