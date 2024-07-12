package org.example

enum class Command(private val commandName: String) {
    SET("SET") {
        override val description = "SET <key> <value> - Store the value for key."

        private lateinit var key: String
        private lateinit var value: String

        override fun setParams(params: List<String>) {
            require(params.size == 2) { "${params.size} parameters found, but 2 expected: SET <key> <value> " }
            key = params[0]
            value = params[1]
        }

        override fun execute(storage: Storage) {
            storage[key] = value
        }
    },

    GET("GET") {
        override val description = "GET <key> - Return the current value for key."

        private lateinit var key: String

        override fun setParams(params: List<String>) {
            require(params.size == 1) { "${params.size} parameters found, but 1 expected: GET <key>" }
            key = params[0]
        }

        override fun execute(storage: Storage) {
            val value = storage[key]
            println(value ?: "key not set")
        }
    },

    DELETE("DELETE") {
        override val description = "DELETE <key> - Remove the entry for key."

        private lateinit var key: String

        override fun setParams(params: List<String>) {
            require(params.size == 1) { "${params.size} parameters found, but 1 expected: DELETE <key>" }
            key = params[0]
        }

        override fun execute(storage: Storage) {
            val value = storage.delete(key)
            if (value == null) {
                println("The key '$key' is not found in the storage.")
            } else {
                println("The key '$key' with value '$value' has been removed.")
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

        override fun execute(storage: Storage) {
            val count = storage.count(value)
            println(count)
        }
    },

    BEGIN("BEGIN") {
        override val description = "BEGIN - Start a new transaction."

        override fun execute(storage: Storage) {
            storage.beginTransaction()
        }
    },

    COMMIT("COMMIT") {
        override val description = "COMMIT - Complete the current transaction."

        override fun execute(storage: Storage) {
            val transactionCount = storage.commitTransaction()
            println("Transaction commited successfully. $transactionCount transaction(s) are pending")
        }
    },

    ROLLBACK("ROLLBACK") {
        override val description = "ROLLBACK - Revert to state prior to BEGIN call."

        override fun execute(storage: Storage) {
            val transactionCount = storage.rollbackTransaction()
            println("Transaction reverted successfully. $transactionCount transaction(s) are pending")
        }
    },

    HELP("HELP") {
        override val description = "HELP - Print all available commands."

        override fun execute(storage: Storage) {
            Command.entries.forEach { command ->
                println(command.description)
            }
        }
    },

    EXIT("EXIT") {
        override val description = "EXIT - Say BYE! and exit. All data in the storage will be lost."

        override fun execute(storage: Storage) {
            println("BYE!")
        }
    };

    abstract val description: String
    abstract fun execute(storage: Storage)
    protected open fun setParams(params: List<String>) {}

    companion object {
        private val allCommands = Command.entries.associateBy { it.name }

        fun getInstance(params: List<String>): Command {
            require(params.isNotEmpty())

            val commandName = params[0].uppercase()
            val command = allCommands[commandName]
                ?: error("Unknown command \"${params[0]}\". Type HELP to get a list of all supported commands.")

            command.setParams(params.subList(1, params.size))
            return command
        }
    }
}
