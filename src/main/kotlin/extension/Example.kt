package org.example.extension

fun String.isPalindrome(): Boolean {
    return this == this.reversed()
}



fun main() {
    val str1 = "racecar"
    val str2 = "Hello, World!"
    println(str1.isPalindrome())  // true
    println(str2.isPalindrome())  // false
}