package org.example

private class TransactionData{
    val modified: Map<String, String>
        field = mutableMapOf()

    val removed: Set<String>
        field = mutableSetOf()

    fun isNotEmpty(): Boolean = modified.isNotEmpty() || removed.isNotEmpty()

    fun set(key: String, value: String) {
        modified[key] = value
        removed -= key
    }

    fun delete(key: String) {
        modified.remove(key)
        removed += key
    }

    fun merge(other: TransactionData) {
        modified += other.modified
        removed -= other.modified.keys
        removed += other.removed
    }
}

class Storage {

    // transactionStack[0] is the root transaction which is impossible to commit or rollback
    private val transactionStack = mutableListOf(TransactionData())

    // merged data from all transactions in the transactionStack
    private val cache = mutableMapOf<String, String>()

    private var isCacheValid = true

    fun count(value: String): Int {
        ensureCache()
        return cache.values.count { it == value }
    }

    operator fun get(key: String): String? {
        ensureCache()
        return cache[key]
    }

    operator fun set(key: String, value: String) {
        isCacheValid = false
        transactionStack.last().set(key, value)
    }

    fun delete(key: String): String? {
        val value = get(key)

        if (value != null) {
            transactionStack.last().delete(key)
            isCacheValid = false
        }

        return value
    }

    fun beginTransaction(): Int {
        transactionStack += TransactionData()
        return transactionStack.size - 1
    }

    fun commitTransaction(): Int {
        if (transactionStack.size == 1) {
            // Only root transaction in stack
            error("no transaction")
        }

        val parent = transactionStack[transactionStack.size - 2]
        val current = transactionStack[transactionStack.size - 1]

        parent.merge(current)

        transactionStack.removeLast()

        return transactionStack.size - 1
    }

    fun rollbackTransaction(): Int {
        if (transactionStack.size == 1) {
            // Only root transaction in stack
            error("no transaction")
        }

        val transaction = transactionStack.removeLast()

        if (transaction.isNotEmpty()) {
            isCacheValid = false
        }

        return transactionStack.size - 1
    }

    private fun ensureCache() {
        if (isCacheValid) {
            return
        }

        cache.clear()

        transactionStack.forEach { transaction ->
            cache += transaction.modified
            cache -= transaction.removed
        }

        isCacheValid = true
    }
}
