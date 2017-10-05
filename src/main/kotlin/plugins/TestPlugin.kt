package plugins

import app.*
import kotlinx.coroutines.experimental.channels.*
import org.jetbrains.ktor.locations.*
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.websocket.*

@location("/")
class Index

object TestPlugin : Plugin() {

    override val id = "fknjansletsloerienublij#userfriendly&stuff"

    override fun PluginConfig.configuration() {
        router("test") {
            get<Index> {
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

        dependencies {
            //bind<T>() with singleton { T() }
            //todo: own dsl? default kodein dsl?
        }
    }
}
