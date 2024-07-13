package org.example

import org.example.storage.Storage
import org.example.storage.newStorage

fun main() {
    println("Welcome to an interactive interface to a transactional key value store.")
    println("Type HELP to get a list of all supported commands.")

    val storage = Storage.newStorage()
    var command: Command? = null

    while (command != Command.EXIT) {
        print("> ")

        val commandLine = readln().split(' ').filter { it.isNotBlank() }

        if (commandLine.isEmpty()) {
            continue
        }

        try {
            command = Command.getInstance(commandLine)
            val output = command.execute(storage)
            output?.let { println(it) }
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
