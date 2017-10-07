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
        val pluginManager = PluginManager(this)

        fun Plugin.install() = pluginManager.install(this)
        fun Plugin.uninstall() = pluginManager.uninstall(this)

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


