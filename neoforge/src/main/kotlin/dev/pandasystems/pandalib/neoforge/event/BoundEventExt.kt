package dev.pandasystems.pandalib.neoforge.event

import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.Subscription
import dev.pandasystems.pandalib.event.platformEvent
import net.neoforged.bus.api.IEventBus
import java.util.function.Consumer
import net.neoforged.bus.api.Event as NeoForgeEvent

inline fun <reified E : NeoForgeEvent, T> IEventBus.bindEvent(
	crossinline convertToCtx: (E) -> T,
	crossinline convertFromCtx: (T) -> E
): Event<T> = platformEvent(
	name = E::class.java.simpleName,
	onSubscribe = { listener ->
		val consumer = Consumer<E> { e -> listener(convertToCtx(e)) }
		this.addListener(E::class.java, consumer)
		Subscription { this.unregister(consumer) }
	},
	onInvoke = { context ->
		val eventInstance = convertFromCtx(context)
		convertToCtx(this.post(eventInstance))
	}
)