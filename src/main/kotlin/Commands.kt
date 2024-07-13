package org.example

import org.example.storage.Storage

/**
 * Enum representing CLI commands. Easily extendable by adding new items.
 * New commands automatically integrate with the HELP command output and are immediately functional.
 */
enum class Command(private val commandName: String) {
    SET("SET") {
        override val description = "SET <key> <value> - Store the value for key."

        private lateinit var key: String
        private lateinit var value: String

        override fun setParams(params: List<String>) {
            require(params.size == 2) { "${params.size} parameters found, but 2 expected: SET <key> <value>" }
            key = params[0]
            value = params[1]
        }

        override fun execute(storage: Storage): String? {
            storage[key] = value
            return null
        }
    },

    GET("GET") {
        override val description = "GET <key> - Return the current value for key."

        private lateinit var key: String

        override fun setParams(params: List<String>) {
            require(params.size == 1) { "${params.size} parameters found, but 1 expected: GET <key>" }
            key = params[0]
        }

        override fun execute(storage: Storage): String? {
            return storage[key] ?: "key not set"
        }
    },

    DELETE("DELETE") {
        override val description = "DELETE <key> - Remove the entry for key."

        private lateinit var key: String

        override fun setParams(params: List<String>) {
            require(params.size == 1) { "${params.size} parameters found, but 1 expected: DELETE <key>" }
            key = params[0]
        }

        override fun execute(storage: Storage): String? {
            val value = storage.delete(key)
            return if (value == null) {
                "The key '$key' is not found in the storage"
            } else {
                "The key '$key' with value '$value' has been removed"
            }
        }
    },

    COUNT("COUNT") {
        override val description = "COUNT <value> - Return the number of keys that have the given value."

        private lateinit var value: String

        override fun setParams(params: List<String>) {
            require(params.size == 1) { "${params.size} parameters found, but 1 expected: COUNT <value>" }
            value = params[0]
        }

        override fun execute(storage: Storage): String? {
            val count = storage.count(value)
            return count.toString()
        }
    },

    BEGIN("BEGIN") {
        override val description = "BEGIN - Start a new transaction."

        override fun execute(storage: Storage): String? {
            val transactionCount = storage.beginTransaction()
            return "Transaction started. In total $transactionCount transaction(s) are opened."
        }
    },

    COMMIT("COMMIT") {
        override val description = "COMMIT - Complete the current transaction."

        override fun execute(storage: Storage): String? {
            val transactionCount = storage.commitTransaction()
            return "Transaction commited. In total $transactionCount transaction(s) are opened."
        }
    },

    ROLLBACK("ROLLBACK") {
        override val description = "ROLLBACK - Revert to state prior to BEGIN call."

        override fun execute(storage: Storage): String? {
            val transactionCount = storage.rollbackTransaction()
            return "Transaction reverted. In total $transactionCount transaction(s) are opened."
        }
    },

    HELP("HELP") {
        override val description = "HELP - Print all available commands."

        override fun execute(storage: Storage): String? {
            return buildString {
                appendLine("Supported commands:")
                Command.entries.forEach { appendLine(it.description) }
            }.trimEnd()
        }
    },

    EXIT("EXIT") {
        override val description = "EXIT - Say BYE! and exit. All data in the storage will be lost."

        override fun execute(storage: Storage): String? {
            return "BYE!"
        }
    };

    /** Provides a description for the command, used in displaying help information. */
    abstract val description: String

    /** Accepts the command's parameters provided by the user through the CLI. */
    protected open fun setParams(params: List<String>) {}

    /**
     * Executes the command on the provided storage.
     * Returns a message for display on the CLI stdout after execution or null if no message is needed.
     */
    abstract fun execute(storage: Storage): String?

    companion object {
        private val allCommands = Command.entries.associateBy { it.name }

        fun getInstance(params: List<String>): Command {
            require(params.isNotEmpty())

            val commandName = params[0].uppercase()
            val command = allCommands[commandName]
                ?: error("Unknown command \"${params[0]}\". Type HELP to get a list of all supported commands")

            command.setParams(params.subList(1, params.size))
            return command
        }
    }
}
