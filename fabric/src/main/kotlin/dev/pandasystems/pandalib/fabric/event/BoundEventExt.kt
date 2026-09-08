package dev.pandasystems.pandalib.fabric.event

import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.Subscription
import dev.pandasystems.pandalib.event.platformEvent
import java.util.concurrent.atomic.AtomicReference
import net.fabricmc.fabric.api.event.Event as FabricEvent

inline fun <reified E : Any, T> FabricEvent<E>.bindEvent(
	crossinline createListener: (subInvoker: (ctx: T) -> Unit) -> E,
	crossinline onInvoke: (ctx: T, eventInvoker: E) -> Unit,
): Event<T> = platformEvent(
	onSubscribe = { originalListener ->
		val activeListener = AtomicReference<((T) -> Unit)?>(originalListener)

		val listenerWrapper: (T) -> Unit = { ctx ->
			val current = activeListener.get()
			if (current != null) current(ctx)
		}

		val listener = createListener(listenerWrapper)
		this.register(listener)

		Subscription {
			activeListener.set(null)
		}
	},
	onInvoke = { context ->
		onInvoke(context, this.invoker())
		context
	}
)