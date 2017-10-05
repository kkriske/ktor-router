package plugins

import api.*
import app.Response
import kotlinx.coroutines.experimental.channels.consumeEach
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.sessions.*
import org.jetbrains.ktor.util.nextNonce
import org.jetbrains.ktor.websocket.*

object NotificationPlugin : Plugin() {

    override val id = "notification"

    override fun PluginConfig.configuration() {
        router("notification") {
            install(Sessions) {
                cookie<NotificationSession>("SESSION")
            }

            intercept(ApplicationCallPipeline.Infrastructure) {
                if (call.sessions.get<NotificationSession>() == null) {
                    call.sessions.set(NotificationSession(nextNonce()))
                }
            }

            get {
                call.respond(Response("Notifications"))
            }

            webSocket {
                val session = call.sessions.get<NotificationSession>()
                if (session == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No active session"))
                    return@webSocket
                }

                send(Frame.Text("In nomine patre, et filii, et spiritus sanctus"))

                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        when (frame.readText()) {
                            "quit" -> close(CloseReason(CloseReason.Codes.NORMAL, "You closed this shit"))
                            else -> send(Frame.Text("kloothommel"))
                        }
                    }
                }

            }
        }
    }
}

data class NotificationSession(val id: String)

