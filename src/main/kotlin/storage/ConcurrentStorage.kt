package org.example.storage

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

fun Storage.Companion.synchronizedStorage(storage: Storage): Storage = ConcurrentStorage(storage)

private class ConcurrentStorage(private val storage: Storage) : Storage {

    private val lock = ReentrantReadWriteLock()

    override fun count(value: String): Int = lock.readLock().withLock {
        storage.count(value)
    }

    override fun get(key: String): String? = lock.readLock().withLock {
        storage[key]
    }

    override fun set(key: String, value: String): String? = lock.writeLock().withLock {
        storage.set(key, value)
    }

    override fun delete(key: String): String? = lock.writeLock().withLock {
        storage.delete(key)
    }

    override fun beginTransaction(): Int {
        lock.writeLock().lock()
        return storage.beginTransaction()
    }

    override fun commitTransaction(): Int {
        return storage.commitTransaction().also { lock.writeLock().unlock() }
    }

    override fun rollbackTransaction(): Int {
        return storage.rollbackTransaction().also { lock.writeLock().unlock() }
    }
}
