package org.example.nullsafety

data class User(val id: Int, val name: String?)


fun main() {
    val user = Database.getUser(1)

    // Business Code
    println("User id is ${user.id}")

    user.name?.let {
        println("User name is $it")
    }
}

object Database {
    fun getUser(id: Int): User {
        return User(id, "John")
    }
}