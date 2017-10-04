package app

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.features.CORS
import org.jetbrains.ktor.features.CallLogging
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.features.StatusPages
import org.jetbrains.ktor.gson.GsonSupport
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.route
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.websocket.WebSockets
import plugins.NotificationPlugin
import plugins.TestPlugin
import plugins.usermanagement.UserManagementPlugin
import kotlin.collections.set

data class Response(val route: String)
data class Error(val route: String,
                 val status: Int,
                 val message: String)

fun Application.main() {
    install(DefaultHeaders)
    install(Locations)
    install(CallLogging)
    install(WebSockets)
    install(CORS) {
        host("*")
    }
    install(GsonSupport) {
        setPrettyPrinting()
    }
    install(StatusPages) {
        status(HttpStatusCode.NotFound,
                HttpStatusCode.Unauthorized,
                HttpStatusCode.Forbidden,
                HttpStatusCode.InternalServerError) {
            call.response.status(it)
            call.respond(Error(
                    call.request.local.uri,
                    it.value,
                    it.description
            ))
        }
    }
    routing {
        get("/") {
            call.respond(Response("root"))
        }
        api()
    }
}

fun Route.api() {
    route("/api") {
        val map: HashMap<String, Route> = hashMapOf()

        fun Plugin.install() {
            if (map.containsKey(id)) throw DuplicatePluginException("A Plugin with id '$id' already exists.")
            map[id] = route(id, this::routing)
        }

        fun Plugin.uninstall() {
            val router = map.remove(id) ?: throw PluginNotFoundException("There was no plugin with id $id installed.")
            children.remove(router)
        }
        //install TestPlugin by default
        TestPlugin.install()
        NotificationPlugin.install()
        UserManagementPlugin.install()

        get {
            call.respond(Response("api"))
        }
        get("/install") {
            TestPlugin.install()
            NotificationPlugin.install()
            call.respond(Response("install"))
        }
        get("/uninstall") {
            TestPlugin.uninstall()
            NotificationPlugin.uninstall()
            call.respond(Response("uninstall"))
        }
    }
}

private class DuplicatePluginException(message: String) : Exception(message)
private class PluginNotFoundException(message: String) : Exception(message)

