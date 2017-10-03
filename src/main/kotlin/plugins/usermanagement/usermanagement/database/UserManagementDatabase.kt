package plugins.usermanagement.database

import com.mongodb.client.MongoCollection
import org.litote.kmongo.*
import plugins.usermanagement.models.User

object UserManagementDatabase {

    private val collection: MongoCollection<User>

    init {
        val client = KMongo.createClient()
        val database = client.getDatabase(props.databaseName)
        this.collection = database.getCollection()
    }

    fun add(user: User): User {
        this.collection.save(user)
        println(user._id)
        return user
    }

    fun getAll(): List<User> {
        return this.collection.find().toList()
    }

    fun get(id: String): User? {
        return this.collection.findOneById(id)
    }

    fun delete(id: String): Boolean {
        return this.collection.deleteOneById(id).deletedCount.compareTo(1) == 0
    }

}