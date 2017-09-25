package app

import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.features.*
import org.jetbrains.ktor.gson.*
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.*
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.websocket.*
import plugins.*

data class Response(val route: String)
data class Error(val route: String,
                 val status: Int,
                 val message: String)

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
        apiRoute = route("/api") {
            get {
                call.respond(Response("api"))
            }
            get("/install") {
                TestPlugin.install()
                call.respond(Response("install"))
            }
            get("/uninstall") {
                TestPlugin.uninstall()
                call.respond(Response("uninstall"))
            }
        }
        TestPlugin.install()
    }
}

private fun Plugin.install() {
    //todo: check if plugin already exists
    apiRoute?.apply {
        val router = route(id, this@install::routing)
        map.put(id, router)
    }
}

private fun Plugin.uninstall() {
    apiRoute?.apply {
        val router = map.remove(id)
        router?.let {
            children.remove(router)
        }
    }
}
