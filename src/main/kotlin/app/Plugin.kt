package app

import org.jetbrains.ktor.locations.*
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.*

interface Plugin {
    /**
     * the id of the plugin. This will also be the route ("/api/{id}") on which the endpoints are available.
     */
    val id: String

    //fun route(router: (Route.() -> Unit) -> Route): Route
    fun Route.routing()
}

internal fun Plugin.routing(route: Route) = route.routing()

@location("/")
class index

object TestPlugin : Plugin {

    override val id = "test"

    override fun Route.routing() {
        get<index> {
            call.respondText("test plugin")
        }
        get("/a") {
            call.respondText("extra route")
        }
    }
}