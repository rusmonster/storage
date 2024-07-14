package org.example.storage

sealed interface RevertAction {
    data class Set(val key: String, val value: String) : RevertAction
    data class Delete(val key: String) : RevertAction
}

/**
 * A class that stores transaction logs.
 *
 * It has a capacity limit and a maximum depth of nested transactions to avoid memory leaks
 * and to prevent the storage from being overloaded.
 */
class TransactionLogManager(private val capacity: Int, private val maxDepth: Int) {

    private val transactions = mutableListOf<TransactionLog>()

    private var size = 0

    init {
        require(capacity > 0) { "Transaction log capacity should be greater than 0" }
        require(maxDepth > 0) { "Maximum transaction depth should be greater than 0" }
    }

    operator fun plusAssign(action: RevertAction) {
        if (transactions.isEmpty()) {
            // No transaction. So we shouldn't add any action, because of it be never reverted anyway.
            return
        }

        transactions.last() += action
        size++

        if (size > capacity) {
            error("maximum transaction capacity reached")
        }
    }

    fun handleTransactionStarted(): Int {
        if (transactions.size == maxDepth) {
            error("maximum transaction depth reached")
        }

        transactions += TransactionLog()
        return transactions.size
    }

    fun handleTransactionCommited(): Int {
        if (transactions.isEmpty()) {
            error("no transaction")
        }

        val log = transactions.removeLast()

        if (transactions.isNotEmpty()) {
            transactions.last().moveToEnd(log)
        } else {
            size -= log.size
        }

        return transactions.size
    }

    // don't inline in order to keep the transactions field private
    fun handleTransactionRollback(revertAction: (action: RevertAction) -> Unit): Int {
        if (transactions.isEmpty()) {
            error("no transaction")
        }

        val log = transactions.removeLast()
        log.forEachReversed(revertAction)
        size -= log.size

        return transactions.size
    }
}

/**
 * A class that stores a transaction log for single transaction.
 * It is a doubly linked list which allows to move other transaction log to the end in O(1) time complexity
 * which makes its usage in [TransactionLogManager] more efficient compared to a MutableList.
 */
private class TransactionLog {

    var size: Int = 0
        private set

    private lateinit var head: Node
    private lateinit var tail: Node

    init {
        clear()
    }

    fun clear() {
        head = Node()
        tail = Node()

        head.next = tail
        tail.prev = head

        size = 0
    }

    operator fun plusAssign(action: RevertAction) = addLast(action)

    fun addLast(action: RevertAction) {
        val node = Node()
        node.action = action
        node.next = tail
        node.prev = tail.prev

        tail.prev.next = node
        tail.prev = node

        size++
    }

    /** Moves all nodes from the [log] to the end of this and clears the [log]. */
    fun moveToEnd(log: TransactionLog) {
        tail.prev.next = log.head.next
        log.head.next.prev = tail.prev

        tail = log.tail

        size += log.size
        log.clear()
    }

    inline fun forEachReversed(action: (RevertAction) -> Unit) {
        var node = tail.prev

        while (node != head) {
            action(node.action)
            node = node.prev
        }
    }

    private class Node {
        lateinit var action: RevertAction
        lateinit var next: Node
        lateinit var prev: Node
    }
}
