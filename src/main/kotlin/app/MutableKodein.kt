package app

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinContainer

/**
 * A class that can be used to configure a mutable kodein object and as a kodein object.
 *
 * This implementation is based on the ConfigurableKodein implementation at https://github.com/SalomonBrys/Kodein
 */
internal object MutableKodein : Kodein {

    private val _lock = Any()

    private val _map: MutableMap<String, Kodein.Builder.() -> Unit> = hashMapOf()

    private @Volatile
    var _instance: Kodein? = null

    fun addModule(id: String, module: Kodein.Module) {
        synchronized(_lock) {
            _instance = null
            _map[id] = { import(module) }
        }
    }

    fun removeModule(id: String) {
        _map.remove(id)
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
