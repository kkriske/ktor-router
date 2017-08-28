package app

import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.features.*
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*

fun Application.main() {
    install(DefaultHeaders)
    install(Locations)
    routing {
        get("/") {
            call.respondText("root")
        }
        route("/api") {
            get {
                call.respondText("api")
            }
            get("/add") {
                add(TestPlugin)
                call.respondText("add")
            }
            get("/del") {
                del(TestPlugin)
                call.respondText("del")
            }
        }
    }
}

val map: HashMap<String, Route> = hashMapOf()

fun Application.add(plugin: Plugin) {
    routing {
        route("/api") {
            route(plugin.id) {
                val router = apply(plugin::routing)
                map.put(plugin.id, router)
            }
        }
    }
}

fun Application.del(plugin: Plugin) {
    routing {
        route("/api") {
            val router = map.remove(plugin.id)
            router?.let {
                children.remove(router)
            }
        }
    }
}

