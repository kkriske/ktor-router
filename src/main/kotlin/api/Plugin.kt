package api

import com.github.salomonbrys.kodein.*
import org.jetbrains.ktor.pipeline.ContextDsl
import org.jetbrains.ktor.routing.*

abstract class Plugin {

    val config = PluginConfig { configuration() }

    /**
     * the id of the plugin. This will also be the route ("/api/{id}") on which the endpoints are available.
     */
    abstract val id: String

    abstract fun PluginConfig.configuration()
}

@DslMarker
annotation class ConfigDsl

@ContextDsl
@ConfigDsl
class PluginConfig internal constructor(init: PluginConfig.() -> Unit) {

    init {
        init()
    }

    var routerConfig: RouterConfig? = null
        private set

    val providerConfig = ProviderConfig()

    fun router(path: String, block: Route.() -> Unit) {
        routerConfig = RouterConfig(path, block)
    }

    fun provide(config: ProviderConfig.() -> Unit) {
        providerConfig.apply(config)
    }
}

class RouterConfig internal constructor(val path: String, val route: Route.() -> Unit)

@ConfigDsl
class ProviderConfig {

    val module get() = Kodein.Module { list.forEach { apply(it) } }

    @PublishedApi
    internal val list: MutableList<Kodein.Builder.() -> Unit> = arrayListOf()

    inline fun <reified T : Any> singleton(noinline creator: () -> T) {
        list.add { bind<T>() with singleton { creator() } }
    }

}
