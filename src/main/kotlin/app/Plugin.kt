package app

import org.jetbrains.ktor.routing.*

interface Plugin {
    /**
     * the id of the plugin. This will also be the route ("/api/{id}") on which the endpoints are available.
     */
    val id: String

    fun Route.routing()
}

internal fun Plugin.routing(route: Route) = route.routing()
