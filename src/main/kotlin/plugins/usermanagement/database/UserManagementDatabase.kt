package plugins.usermanagement.database

import com.mongodb.client.MongoCollection
import org.litote.kmongo.*
import plugins.usermanagement.models.User
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.*

object UserManagementDatabase {

    private val collection: MongoCollection<User>
    private const val PROPERTIES_EXCEPTION: String = "The UserManagement-plugin requires a database name stored at the key databaseName in a file named config.properties"

    init {
        val prop = Properties()
        try {
            FileInputStream("config.properties").use {
                prop.load(it)
            }
        } catch (e: FileNotFoundException) {
            throw Exception(PROPERTIES_EXCEPTION)
        }

        if (prop["databaseName"] == null) {
            throw Exception(PROPERTIES_EXCEPTION)
        }

        val client = KMongo.createClient()
        val database = client.getDatabase(prop["databaseName"].toString())
        collection = database.getCollection()
    }

    fun add(user: User): User {
        collection.save(user)
        return user
    }

    fun getAll(): List<User> {
        return collection.find().toList()
    }

    fun get(id: String): User? {
        return collection.findOneById(id)
    }

    fun update(id: String, user: User) {
        collection.updateOneById(id, user)
    }

    fun delete(id: String): Boolean {
        return collection.deleteOneById(id).deletedCount.compareTo(1) == 0
    }

}