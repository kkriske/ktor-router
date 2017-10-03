package plugins.usermanagement

import app.Plugin
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get
import plugins.usermanagement.database.UserManagementDatabase
import plugins.usermanagement.models.User

@location("/")
class Index

object UserManagementPlugin : Plugin {

    private val userManagementDatabase: UserManagementDatabase = UserManagementDatabase

    override val id = "user_management"

    override fun Route.routing() {
        get<Index> {
            val users = userManagementDatabase.getAll()
            call.respond(users)
        }

        post<Index> {
            val user = call.receive<User>()
            val result = userManagementDatabase.add(user)
            call.respond(result)
        }

        get("/{id}") {
            val id = call.parameters["id"]!!

            val user = userManagementDatabase.get(id)
            if (user == null) {
                call.respond("User does not exist")
            }
            call.respond(user!!)
        }
    }
}
