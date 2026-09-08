package dev.pandasystems.pandalib.core

import dev.pandasystems.pandalib.core.lifecycles.ClientLifecycle
import dev.pandasystems.pandalib.core.lifecycles.ServerLifecycle


class PandaLibMain {
	init {
		install(this)
		ClientLifecycle.initialize()
		ServerLifecycle.initialize()
	}

	companion object {
		lateinit var instance: PandaLibMain
			private set

		private fun install(main: PandaLibMain) {
			if (!::instance.isInitialized) {
				instance = main
			} else {
				logger.warn("""
					PandaLibMain instance already initialized.
					This is only intended for testing purposes.
				""".trimIndent())
			}
		}
	}
}