package app

import com.github.salomonbrys.kodein.*

/**
 * A configurable mutable kodein object.
 *
 * This implementation is based on the ConfigurableKodein implementation at https://github.com/SalomonBrys/Kodein
 */
class MutableKodein : Kodein {

    private val _lock = Any()
    private val _map: MutableMap<String, Kodein.Builder.() -> Unit> = hashMapOf()

    @Volatile private var _instance: Kodein? = null

    fun addModule(id: String, module: Kodein.Module) {
        synchronized(_lock) {
            _instance = null
            _map[id] = { import(module) }
        }
    }

    fun removeModule(id: String) {
        synchronized(_lock) {
            _map.remove(id)?.let {
                _instance = null
            }
        }
    }

    private fun getOrConstruct(): Kodein {
        _instance?.let { return it }
        synchronized(_lock) {
            val instance = Kodein {
                _map.values.forEach { apply(it) }
            }
            _instance = instance
            return instance
        }
    }

    override val container: KodeinContainer get() = getOrConstruct().container

}
