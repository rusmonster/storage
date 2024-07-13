package org.example

class CountedMap<K, V> {

    private val data = mutableMapOf<K, V>()

    private val counters = mutableMapOf<V, Int>()

    fun count(value: V) = counters.getOrDefault(value, 0)

    operator fun get(key: K) = data[key]

    operator fun set(key: K, value: V) {
        val oldValue = data[key]
        data[key] = value

        oldValue?.let { counters[it] = counters[it]!! - 1 }
        counters[value] = counters.getOrDefault(value, 0) + 1
    }

    fun remove(key: K): V? {
        val oldValue = data.remove(key)
        oldValue?.let { counters[it] = counters[it]!! - 1 }
        return oldValue
    }
}
