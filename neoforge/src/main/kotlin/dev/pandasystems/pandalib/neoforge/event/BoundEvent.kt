package dev.pandasystems.pandalib.neoforge.event

import dev.pandasystems.pandalib.event.Event
import dev.pandasystems.pandalib.event.Subscription
import net.neoforged.bus.api.IEventBus
import java.util.function.Consumer
import net.neoforged.bus.api.Event as NeoForgeEvent

class BoundEvent<T, R, E: NeoForgeEvent>(
	val eventBus: IEventBus,
	val eventClass: Class<E>,
	val convertToCtx: (E) -> T,
	val convertFromCtx: (T) -> E,
	val supplyReturn: (E) -> R
) : Event<T, R> {
	override val name: String
		get() = TODO("Not yet implemented")

	override fun subscribe(listener: (context: T) -> R): Subscription {
		val consumer: Consumer<E> = { e ->
			val ctx = convertToCtx(e)
			listener(ctx)
		}
		eventBus.addListener(eventClass, consumer)
		return Subscription {
			eventBus.unregister(consumer)
		}
	}

	override fun invoke(context: T): R {
		val result = eventBus.post(convertFromCtx(context))
		return supplyReturn(result)
	}
}