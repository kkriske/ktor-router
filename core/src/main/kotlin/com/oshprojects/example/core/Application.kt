package com.oshprojects.example.core

import com.oshprojects.example.api.Plugin
import com.oshprojects.example.notification.NotificationPlugin
import com.oshprojects.example.testplugin.TestPlugin
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.features.*
import org.jetbrains.ktor.gson.*
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.*
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.websocket.*

data class Response(val route: String)
data class Error(val route: String,
                 val status: Int,
                 val message: String)

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
        api()
    }
}

fun Route.api() {
    route("/api") {
        val map: HashMap<String, Route> = hashMapOf()
        val kodein = MutableKodein()

        fun Plugin.install() {
            if (map.containsKey(id)) throw DuplicatePluginException("A Plugin with id '$id' already exists.")
            //TODO: check the uniqueness of the registered path
            config.routerConfig?.let { map[id] = route(it.path, it.route) }
            kodein.addModule(id, config.providerConfig.module)
        }

        fun Plugin.uninstall() {
            //TODO: if a plugin does not specify routes, map.remove returns `null` and the error should not be thrown.
            val router = map.remove(id) ?: throw PluginNotFoundException("There was no plugin with id $id installed.")
            children.remove(router)
            kodein.removeModule(id)
        }
        //install TestPlugin by default
        TestPlugin.install()
        NotificationPlugin.install()

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

