package org.example.storage

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

private typealias TransactionLog = MutableList<RevertAction>

private sealed interface RevertAction {
    class Set(val key: String, val value: String) : RevertAction
    class Delete(val key: String) : RevertAction
}

class Storage {

    private val data = CountedMap<String, String>()

    private val transactionLogs = mutableListOf<TransactionLog>()

    private val lock = ReentrantReadWriteLock()

    fun count(value: String): Int = lock.readLock().withLock {
        data.count(value)
    }

    operator fun get(key: String): String? = lock.readLock().withLock {
        return data[key]
    }

    operator fun set(key: String, value: String): String? = lock.writeLock().withLock {
        val oldValue = data[key]

        transactionLogs.lastOrNull()?.let { transactionLog ->
            transactionLog += if (oldValue == null) RevertAction.Delete(key) else RevertAction.Set(key, oldValue)
        }

        data[key] = value
        return oldValue
    }

    fun delete(key: String): String? = lock.writeLock().withLock {
        val oldValue = data.remove(key)

        transactionLogs.lastOrNull()?.let { transactionLog ->
            oldValue?.let { transactionLog += RevertAction.Set(key, it) }
        }

        return oldValue
    }

    fun beginTransaction(): Int {
        lock.writeLock().lock()
        transactionLogs += mutableListOf<RevertAction>()
        return transactionLogs.size
    }

    fun commitTransaction(): Int {
        if (transactionLogs.isEmpty()) {
            error("no transaction")
        }

        val transactionLog = transactionLogs.removeLast()

        if (transactionLogs.isNotEmpty()) {
            transactionLogs.last() += transactionLog
        }

        val size = transactionLogs.size
        lock.writeLock().unlock()
        return size
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

        val size = transactionLogs.size
        lock.writeLock().unlock()
        return size
    }
}
