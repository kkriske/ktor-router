package app

import com.github.salomonbrys.kodein.*

/**
 * A configurable mutable kodein object.
 *
 * This implementation is based on the ConfigurableKodein implementation at https://github.com/SalomonBrys/Kodein
 */
internal object MutableKodein : Kodein {

    private val _lock = Any()

    private val _map: MutableMap<String, Kodein.Builder.() -> Unit> = hashMapOf()

    private @Volatile
    var _instance: Kodein? = null

    fun addModule(id: String, module: Kodein.Module) = lock { _map[id] = { import(module) } }

    fun removeModule(id: String) = lock { _map.remove(id) }

    private fun lock(block: MutableKodein.() -> Unit) = synchronized(_lock) { _instance = null; apply(block) }

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
