package org.example

fun main() {
    println("Welcome to an interactive interface to a transactional key value store.")
    println("Type HELP to get a list of all supported commands.")

    val storage = Storage()
    var command: Command? = null

    while (command != Command.EXIT) {
        print("> ")

        val params = readln().split(' ').filter { it.isNotBlank() }

        if (params.isEmpty()) {
            continue
        }

        try {
            command = Command.getInstance(params)
            command.execute(storage)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
