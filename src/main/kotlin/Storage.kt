package org.example

class Storage {

    // transactionStack[0] is the root transaction which is impossible to commit or rollback
    private val transactionStack = mutableListOf(mutableMapOf<String, String>())

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
        transactionStack.last().put(key, value)
    }

    fun delete(key: String): String? {
        isCacheValid = false
        return transactionStack.last().remove(key)
    }

    fun beginTransaction(): Int {
        transactionStack += mutableMapOf()
        return transactionStack.size - 1
    }

    fun commitTransaction(): Int {
        if (transactionStack.size == 1) {
            // Only root transaction in stack
            error("no transaction")
        }

        transactionStack[transactionStack.size - 2] += transactionStack[transactionStack.size - 1]
        transactionStack.removeLast()

        return transactionStack.size - 1
    }

    fun rollbackTransaction(): Int {
        if (transactionStack.size == 1) {
            // Only root transaction in stack
            error("no transaction")
        }

        transactionStack.removeLast()
        isCacheValid = false

        return transactionStack.size - 1
    }

    private fun ensureCache() {
        if (isCacheValid) {
            return
        }

        cache.clear()
        transactionStack.forEach { cache += it }

        isCacheValid = true
    }
}
