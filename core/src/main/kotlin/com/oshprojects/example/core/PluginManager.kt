package com.oshprojects.example.core

import com.oshprojects.example.api.Plugin
import org.jetbrains.ktor.routing.*

class DuplicatePluginException(message: String) : Exception(message)
class DuplicateRouteException(message: String) : Exception(message)

class PluginManager(private val parentRoute: Route) {

    private val _kodein = MutableKodein()
    private val _installedPlugins = hashSetOf<String>()
    private val _routeMap = hashMapOf<String, Route>()
    private val _lock = Any()

    fun install(plugin: Plugin) {
        with(plugin) {
            if (_installedPlugins.contains(id))
                throw DuplicatePluginException("A Plugin with id '$id' already exists.")

            synchronized(_lock) {
                config.routerConfig?.let {
                    if (_routeMap.containsKey(it.path))
                        throw DuplicateRouteException("A Route with path '${it.path}' already exists.")
                    _routeMap[it.path] = parentRoute.route(it.path, it.route)
                }
                _installedPlugins.add(id)
                _kodein.addModule(id, config.providerConfig.module)
            }
        }
    }

    fun uninstall(plugin: Plugin) {
        with(plugin) {
            synchronized(_lock) {
                if (_installedPlugins.remove(id)) {
                    config.routerConfig?.let {
                        _routeMap.remove(it.path)?.let(parentRoute.children::remove) ?:
                                throw IllegalStateException("congratulations, you've reached an impossible state!")
                    }
                    _kodein.removeModule(id)
                }
            }
        }
    }

}
