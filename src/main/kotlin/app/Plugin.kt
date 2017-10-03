package app

import com.github.salomonbrys.kodein.Kodein
import org.jetbrains.ktor.routing.Route

@DslMarker
annotation class PluginDsl

abstract class Plugin {

    internal val config = PluginConfig().also { it.configuration() }

    /**
     * the id of the plugin. This will also be the route ("/api/{id}") on which the endpoints are available.
     */
    abstract val id: String

    abstract fun PluginConfig.configuration()
}

@PluginDsl
class PluginConfig internal constructor() {


    internal var _router: Route.() -> Unit = {}
        private set
    internal var _kodein: Kodein.Module = Kodein.Module {}
        private set

    fun router(block: Route.() -> Unit) {
        _router = block
    }

    //TODO: kodein config
    fun dependencies(block: Kodein.Builder.() -> Unit) {
        _kodein = Kodein.Module(init = block)
    }
}

//internal fun Plugin.routing(route: Route) = route.routing()
