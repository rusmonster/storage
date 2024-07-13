package org.example.storage

interface Storage {
    fun count(value: String): Int
    operator fun get(key: String): String?
    operator fun set(key: String, value: String): String?
    fun delete(key: String): String?

    fun beginTransaction(): Int
    fun commitTransaction(): Int
    fun rollbackTransaction(): Int

    companion object // To make it possible to add static extensions
}
