package app

import com.github.salomonbrys.kodein.*
import org.jetbrains.ktor.routing.Route

abstract class PluginOld {

    internal val config = PluginConfigOld().also { it.configuration() }

    /**
     * the id of the plugin. This will also be the route ("/api/{id}") on which the endpoints are available.
     */
    abstract val id: String

    abstract fun PluginConfigOld.configuration()
}


internal class RouterConfigOld(val path: String, val route: Route.() -> Unit)

@DslMarker
annotation class PluginDsl

@PluginDsl
class PluginConfigOld internal constructor() {


    internal var _routerConfig: RouterConfigOld? = null
        private set
    internal var _kodein: Kodein.Module = Kodein.Module {}
        private set

    fun router(path: String, block: Route.() -> Unit) {
        _routerConfig = RouterConfigOld(path, block)
    }

    //TODO: kodein config
    fun dependencies(block: Kodein.Builder.() -> Unit) {
        _kodein = Kodein.Module(init = block)
    }

    fun provide(config: ProviderDslOld.() -> Unit) {

    }
}

@PluginDsl
class ProviderDslOld {

    val kodein = Kodein.Module(false, { })

    private val list: MutableList<Kodein.Builder.() -> Unit> = arrayListOf()

    val f: (Kodein.Builder.() -> Unit)->Unit = {
        list.add(it)
    }

    inline fun <reified T: Any> singleton(noinline creator: () -> T) {
        f {
            bind<T>() with singleton { creator() }
        }
    }

}

//internal fun Plugin.routing(route: Route) = route.routing()
