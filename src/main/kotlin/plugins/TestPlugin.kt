package plugins

import app.*
import kotlinx.coroutines.experimental.channels.*
import org.jetbrains.ktor.locations.*
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.websocket.*

@location("/")
class index

object TestPlugin : Plugin {

    override val id = "test"

    override fun Route.routing() {
        get<index> {
            call.respond(Response("test plugin"))
        }
        get("/a") {
            call.respond(Response("extra route"))
        }
        webSocket("/ws") {
            incoming.consumeEach {
                send(it)
            }
        }
    }
}
