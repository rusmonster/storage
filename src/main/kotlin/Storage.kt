package org.example

private typealias TransactionLog = MutableList<RevertAction>

private sealed interface RevertAction {
    class Set(val key: String, val value: String) : RevertAction
    class Delete(val key: String) : RevertAction
}

class Storage {

    private val data = mutableMapOf<String, String>()

    private val transactionLogs = mutableListOf<TransactionLog>()

    fun count(value: String): Int {
        return data.values.count { it == value }
    }

    operator fun get(key: String): String? {
        return data[key]
    }

    operator fun set(key: String, value: String): String? {
        val oldValue = data[key]

        transactionLogs.lastOrNull()?.let { transactionLog ->
            transactionLog += if (oldValue == null) RevertAction.Delete(key) else RevertAction.Set(key, oldValue)
        }

        data[key] = value
        return oldValue
    }

    fun delete(key: String): String? {
        val oldValue = data.remove(key)

        transactionLogs.lastOrNull()?.let { transactionLog ->
            oldValue?.let { transactionLog += RevertAction.Set(key, oldValue) }
        }

        return oldValue
    }

    fun beginTransaction(): Int {
        transactionLogs += mutableListOf<RevertAction>()
        return transactionLogs.size
    }

    fun commitTransaction(): Int {
        if (transactionLogs.isEmpty()) {
            error("no transaction")
        }

        transactionLogs.removeLast()

        return transactionLogs.size
    }

    fun rollbackTransaction(): Int {
        if (transactionLogs.isEmpty()) {
            error("no transaction")
        }

        val transactionLog = transactionLogs.removeLast()

        for (i in transactionLog.lastIndex downTo 0) {
            when (val action = transactionLog[i]) {
                is RevertAction.Set -> data[action.key] = action.value
                is RevertAction.Delete -> data.remove(action.key)
            }
        }

        return transactionLogs.size
    }
}
