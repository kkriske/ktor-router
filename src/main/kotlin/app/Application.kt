package app

import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.features.*
import org.jetbrains.ktor.gson.*
import org.jetbrains.ktor.locations.*
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.websocket.*
import plugins.*

data class Response(val route: String)

private var apiRoute: Route? = null
private val map: HashMap<String, Route> = hashMapOf()

fun Application.main() {
    install(DefaultHeaders)
    install(Locations)
    install(CallLogging)
    install(WebSockets)
    install(GsonSupport) {
        setPrettyPrinting()
    }
    routing {
        get("/") {
            call.respond(Response("root"))
        }
        apiRoute = route("/api") {
            get {
                call.respond(Response("api"))
            }
            get("/add") {
                add(TestPlugin)
                call.respond(Response("add"))
            }
            get("/del") {
                del(TestPlugin)
                call.respond(Response("del"))
            }
        }
        add(TestPlugin)
    }
}

private fun add(plugin: Plugin) {
    apiRoute?.apply {
        val router = route(plugin.id) {
            apply(plugin::routing)
        }
        map.put(plugin.id, router)
    }
}

private fun del(plugin: Plugin) {
    apiRoute?.apply {
        val router = map.remove(plugin.id)
        router?.let {
            children.remove(router)
        }
    }
}
