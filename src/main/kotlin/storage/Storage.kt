package org.example.storage

/**
 * Interface for a transactional key-value storage.
 * The storage is able to store and retrieve key-value pairs.
 *
 * The storage supports nested transactions, which can be started,
 * committed or rolled back.
 *
 * Maximum transaction log capacity and maximum transaction depth are limited in order to avoid
 * memory leaks and to prevent the storage from being overloaded.
 *
 * Use the [newStorage] factory method to create a new instance of the storage.
 *
 * The storage is not thread-safe by default. To make it thread-safe,
 * use the [synchronizedStorage] method.
 */
interface Storage {

    fun count(value: String): Int

    /** @return The value associated with the key, or null if the key is not found. */
    operator fun get(key: String): String?

    /**
     * @return The previous value associated with the key, or null if the key was not found.
     * @throws IllegalStateException if the transaction capacity is exceeded.
     */
    operator fun set(key: String, value: String): String?

    /**
     * @return The value that was associated with the key, or null if the key was not found.
     * @throws IllegalStateException if the transaction capacity is exceeded.
     */
    fun delete(key: String): String?

    /**
     * @return The stack depth of nested transactions after starting a new transaction.
     * @throws IllegalStateException if the transaction depth is exceeded.
     */
    fun beginTransaction(): Int

    /**
     * @return The stack depth of nested transactions after the current transaction has been committed.
     * @throws IllegalStateException if there is no transaction to commit.
     */
    fun commitTransaction(): Int

    /**
     * @return The stack depth of nested transactions after the current transaction has been rolled back.
     * @throws IllegalStateException if there is no transaction to roll back.
     */
    fun rollbackTransaction(): Int

    companion object // To make it possible to add static extensions
}
