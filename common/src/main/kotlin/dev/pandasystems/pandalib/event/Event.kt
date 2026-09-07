package dev.pandasystems.pandalib.event

import dev.pandasystems.pandalib.core.infoThrottled
import dev.pandasystems.pandalib.core.logger
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Event<T, R> : ReadOnlyProperty<Any?, Event<T, R>> {
    val name: String

    fun subscribe(listener: (context: T) -> R): Subscription
    operator fun invoke(context: T): R

    override fun getValue(thisRef: Any?, property: KProperty<*>): Event<T, R> = this
}

fun interface Subscription {
    fun unsubscribe()
}

fun interface EventProvider<T, R> : PropertyDelegateProvider<Any?, Event<T, R>> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): Event<T, R>
}

fun <T, R> event(
    name: String,
    createInvoker: (listeners: List<(context: T) -> R>, context: T) -> R,
): Event<T, R> {
    logger.infoThrottled(
        key = "event_created_$name",
        messageSupplier = { "Created new event named $name" }
    )
    val listeners = CopyOnWriteArrayList<(context: T) -> R>()

    return object : Event<T, R> {
        override val name: String = name

        override fun subscribe(listener: (context: T) -> R): Subscription {
            listeners += listener
            logger.infoThrottled(
                key = "event_subscribed_$name",
                messageSupplier = { "Listener subscribed to event $name" }
            )

            val subscribed = AtomicBoolean(true)
            return Subscription {
                if (subscribed.compareAndSet(true, false)) {
                    listeners -= listener
                    logger.infoThrottled(
                        key = "event_unsubscribed_$name",
                        messageSupplier = { "Listener unsubscribed from event $name" }
                    )
                }
            }
        }

        override fun invoke(context: T): R {
            logger.infoThrottled(
                key = "event_invoked_$name",
                messageSupplier = { "Invoking event $name with context: $context" }
            )
            return createInvoker(listeners, context)
        }
    }
}

/**
 * Creates an Event implementation that proxies registration and invocation
 * directly to a platform modding API (like NeoForge or Fabric).
 */
fun <T, R> platformEvent(
    name: String,
    onSubscribe: (listener: (context: T) -> R) -> Subscription,
    onInvoke: (context: T) -> R
): Event<T, R> = object : Event<T, R> {
    override val name: String = name

    override fun subscribe(listener: (context: T) -> R): Subscription {
        val subscription = onSubscribe(listener)
        logger.infoThrottled(
            key = "event_subscribed_$name",
            messageSupplier = { "Listener subscribed to platform event $name" }
        )
        return Subscription {
            subscription.unsubscribe()
            logger.infoThrottled(
                key = "event_unsubscribed_$name",
                messageSupplier = { "Listener unsubscribed from platform event $name" }
            )
        }
    }

    override fun invoke(context: T): R {
        logger.infoThrottled(
            key = "event_invoked_$name",
            messageSupplier = { "Invoking platform event $name with context: $context" }
        )
        return onInvoke(context)
    }
}

fun <T, R> event(
    createInvoker: (listeners: List<(context: T) -> R>, context: T) -> R,
): EventProvider<T, R> = EventProvider { _, property ->
    event(property.name, createInvoker)
}

inline fun <reified T> event(name: String): Event<T, Unit> = event(name) { listeners, context ->
    listeners.forEach { it(context) }
}

inline fun <reified T> event(): EventProvider<T, Unit> = EventProvider { _, property ->
    event<T>(property.name)
}

inline fun <reified T> eventCancelable(name: String): Event<T, Boolean> = event(name) { listeners, context ->
    listeners.map { it(context) }.all { it }
}

inline fun <reified T> eventCancelable(): EventProvider<T, Boolean> = EventProvider { _, property ->
    eventCancelable<T>(property.name)
}