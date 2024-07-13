package org.example.storage

fun Storage.Companion.newStorage(): Storage = StorageImpl()

private typealias TransactionLog = MutableList<RevertAction>

private sealed interface RevertAction {
    data class Set(val key: String, val value: String) : RevertAction
    data class Delete(val key: String) : RevertAction
}

private class StorageImpl : Storage {

    private val data = CountedMap<String, String>()

    private val transactionLogs = mutableListOf<TransactionLog>()

    override fun count(value: String): Int = data.count(value)

    override operator fun get(key: String): String? = data[key]

    override operator fun set(key: String, value: String): String? {
        val oldValue = data[key]

        transactionLogs.lastOrNull()?.let { transactionLog ->
            transactionLog += if (oldValue == null) RevertAction.Delete(key) else RevertAction.Set(key, oldValue)
        }

        data[key] = value
        return oldValue
    }

    override fun delete(key: String): String? {
        val oldValue = data.remove(key)

        transactionLogs.lastOrNull()?.let { transactionLog ->
            oldValue?.let { transactionLog += RevertAction.Set(key, it) }
        }

        return oldValue
    }

    override fun beginTransaction(): Int {
        transactionLogs += mutableListOf<RevertAction>()
        return transactionLogs.size
    }

    override fun commitTransaction(): Int {
        if (transactionLogs.isEmpty()) {
            error("no transaction")
        }

        val transactionLog = transactionLogs.removeLast()

        if (transactionLogs.isNotEmpty()) {
            transactionLogs.last() += transactionLog
        }

        return transactionLogs.size
    }

    override fun rollbackTransaction(): Int {
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

        val size = transactionLogs.size
        return size
    }
}
