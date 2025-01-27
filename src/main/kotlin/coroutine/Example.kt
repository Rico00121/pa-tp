package org.example.coroutine

import kotlinx.coroutines.*
import org.example.coroutine.Example.fetchUser
import org.example.coroutine.Example.fetchUserDetails

object Example {
    suspend fun fetchUser(): Map<String, Any> {
        delay(2000) // simulate network latency
        return mapOf("id" to 1, "name" to "Alice")
    }

    suspend fun fetchUserDetails(userId: Int): Map<String, Any> {
        delay(1000) // simulate network latency
        return mapOf("userId" to userId, "email" to "alice@example.com")
    }
}
fun main(): Unit = runBlocking {
        launch {
            val user = fetchUser()
            println("User fetched: $user")
        }
        launch {
            val details = fetchUserDetails(userId = 1)
            println("User details fetched: $details")
        }
}