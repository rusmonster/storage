package org.example.storage

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * Returns a thread-safe decorator for a given [Storage] instance.
 *
 * Thread safety was not in explicit requirements.
 * So this is a trivial thread-safety solution created just for fun.
 *
 * It uses a [ReentrantReadWriteLock] to synchronize access to the underlying storage.
 * So this code works in JVM only.
 *
 * It's possible to build better thread-safe implementation where locking on read operations
 * could be avoided in most cases. But it would require more complex synchronization logic
 * and more space complexity.
 *
 * Covered by tests in [ConcurrentStorageTest].
 */
fun Storage.Companion.synchronizedStorage(storage: Storage): Storage = ConcurrentStorage(storage)

private class ConcurrentStorage(private val storage: Storage) : Storage {

    private val lock = ReentrantReadWriteLock()

    override fun count(value: String) = lock.readLock().withLock { storage.count(value) }

    override fun get(key: String) = lock.readLock().withLock { storage[key] }

    override fun set(key: String, value: String) = lock.writeLock().withLock { storage.set(key, value) }

    override fun delete(key: String) = lock.writeLock().withLock { storage.delete(key) }

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
