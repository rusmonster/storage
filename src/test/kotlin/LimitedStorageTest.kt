import org.example.storage.Storage
import org.example.storage.newStorage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class LimitedStorageTest {

    private lateinit var storage: Storage

    @BeforeTest
    fun setUp() {
        storage = Storage.newStorage(transactionLogCapacity = 2, maxTransactionsDepth = 2)
    }

    @Test
    fun maximumTransactionCapacityCommit() {
        storage["foo"] = "0"
        storage["foo"] = "1"
        storage["foo"] = "2" // no transaction. all 3 set operations succeeded.

        storage.beginTransaction()
        storage["foo"] = "3"
        storage["foo"] = "4"

        val exception = assertFails { storage["foo"] = "5" }
        assertEquals("maximum transaction capacity reached", exception.message)
        assertEquals("4", storage["foo"])

        storage.commitTransaction()
        assertEquals("4", storage["foo"])
    }

    @Test
    fun maximumTransactionCapacityRollback() {
        storage["foo"] = "0"
        storage["foo"] = "1"
        storage["foo"] = "2" // no transaction. all 3 set operations succeeded.

        storage.beginTransaction()
        storage["foo"] = "3"
        storage["foo"] = "4"

        val exception = assertFails { storage["foo"] = "5" }
        assertEquals("maximum transaction capacity reached", exception.message)
        assertEquals("4", storage["foo"])

        storage.rollbackTransaction()
        assertEquals("2", storage["foo"])
    }

    @Test
    fun maximumTransactionCapacityNested() {
        storage.beginTransaction()
        storage["foo"] = "1"

        storage.beginTransaction()
        storage["foo"] = "2"

        val exception = assertFails { storage["foo"] = "3" }
        assertEquals("maximum transaction capacity reached", exception.message)
        assertEquals("2", storage["foo"])
    }

    @Test
    fun maximumTransactionDepth() {
        storage.beginTransaction()
        storage["foo"] = "1"

        storage.beginTransaction()
        storage["foo"] = "2"

        val exception = assertFails { storage.beginTransaction() }
        assertEquals("maximum transaction depth reached", exception.message)

        storage.commitTransaction()
        val depth = storage.commitTransaction()

        assertEquals(0, depth)
        assertEquals("2", storage["foo"])
    }
}
