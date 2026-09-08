package dev.pandasystems.pandalib.event

import dev.pandasystems.pandalib.core.infoThrottled
import dev.pandasystems.pandalib.core.logger
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Event<T> : ReadOnlyProperty<Any?, Event<T>> {
    val name: String

    fun subscribe(listener: (context: T) -> Unit): Subscription
    operator fun invoke(context: T): T

    override fun getValue(thisRef: Any?, property: KProperty<*>): Event<T> = this
}

fun interface Subscription {
    fun unsubscribe()
}

fun interface EventProvider<T> : PropertyDelegateProvider<Any?, Event<T>> {
    override fun provideDelegate(thisRef: Any?, property: KProperty<*>): Event<T>
}

fun <T> platformEvent(
    name: String,
    onSubscribe: (listener: (context: T) -> Unit) -> Subscription,
    onInvoke: (context: T) -> T
): Event<T> = object : Event<T> {
    override val name: String = name

    override fun subscribe(listener: (context: T) -> Unit): Subscription {
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

    override fun invoke(context: T): T {
        logger.infoThrottled(
            key = "event_invoked_$name",
            messageSupplier = { "Invoking platform event $name with context: $context" }
        )
        return onInvoke(context)
    }
}

@PublishedApi
internal fun <T> internalEvent(
    name: String
): Event<T> {
    logger.infoThrottled(
        key = "event_created_$name",
        messageSupplier = { "Created new event named $name" }
    )
    val listeners = CopyOnWriteArrayList<(context: T) -> Unit>()

    return object : Event<T> {
        override val name: String = name

        override fun subscribe(listener: (context: T) -> Unit): Subscription {
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

        override fun invoke(context: T): T {
            logger.infoThrottled(
                key = "event_invoked_$name",
                messageSupplier = { "Invoking event $name with context: $context" }
            )
            listeners.forEach { it(context) }
            return context
        }
    }
}

fun <T> event(): EventProvider<T> = EventProvider { _, property ->
    internalEvent(property.name)
}

inline fun <reified T> event(name: String): Event<T> = internalEvent(name)