package org.example.storage

/**
 * A factory method to create new instances of [Storage].
 * Implementation returned by this factory method is not thread-safe.
 *
 * Use the [synchronizedStorage] method to get a thread-safe instance of [Storage].
 *
 * Maximum transaction log capacity and maximum transaction depth are limited in order to avoid
 * memory leaks and to prevent the storage from being overloaded.
 *
 * @param transactionLogCapacity The maximum number of actions that can be stored in the transaction log.
 * @param maxTransactionsDepth The maximum depth of nested transactions.
 */
fun Storage.Companion.newStorage(transactionLogCapacity: Int = 10000, maxTransactionsDepth: Int = 1000): Storage {
    return StorageImpl(transactionLogCapacity, maxTransactionsDepth)
}

private class StorageImpl(transactionLogCapacity: Int, maxTransactionsDepth: Int) : Storage {

    private val data = CountedMap<String, String>()

    private val transactionLogManager = TransactionLogManager(transactionLogCapacity, maxTransactionsDepth)

    override fun count(value: String): Int = data.count(value)

    override operator fun get(key: String): String? = data[key]

    override operator fun set(key: String, value: String): String? {
        val oldValue = data[key]

        if (value != oldValue) { // to save transactionLogCapacity a little bit
            transactionLogManager += if (oldValue == null) RevertAction.Delete(key) else RevertAction.Set(key, oldValue)
            data[key] = value
        }

        return oldValue
    }

    override fun delete(key: String): String? {
        val oldValue = data.remove(key)

        oldValue?.let { transactionLogManager += RevertAction.Set(key, it) }

        return oldValue
    }

    override fun beginTransaction() = transactionLogManager.handleTransactionStarted()

    override fun commitTransaction() = transactionLogManager.handleTransactionCommited()

    override fun rollbackTransaction() = transactionLogManager.handleTransactionRollback { action ->
        when (action) {
            is RevertAction.Set -> data[action.key] = action.value
            is RevertAction.Delete -> data.remove(action.key)
        }
    }
}
