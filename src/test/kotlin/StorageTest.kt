import org.example.Storage
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class StorageTest {

    lateinit var storage: Storage

    @BeforeTest
    fun setUp() {
        storage = Storage()
    }

    @Test
    fun setAndGet() {
        storage["foo"] = "125"
        assertEquals("125", storage["foo"])
    }

    @Test
    fun delete() {
        assertNull(storage.delete("foo"))
    }

    @Test
    fun count() {
        storage["foo"] = "123"
        storage["bar"] = "456"
        storage["baz"] = "123"

        assertEquals(2, storage.count("123"))
        assertEquals(1, storage.count("456"))
    }

    @Test
    fun commitTransaction() {
        storage["bar"] = "123"
        assertEquals("123", storage["bar"])

        storage.beginTransaction()
        storage["foo"] = "456"
        assertEquals("123", storage["bar"])

        storage.delete("bar")
        storage.commitTransaction()

        assertNull(storage["bar"])

        val exception = assertFails { storage.rollbackTransaction() }
        assertEquals("no transaction", exception.message)

        assertEquals("456", storage["foo"])
    }

    @Test
    fun rollbackTransaction() {
        storage["foo"] = "123"
        storage["bar"] = "abc"

        storage.beginTransaction()
        storage["foo"] = "456"
        assertEquals("456", storage["foo"])

        storage["bar"] = "def"
        assertEquals("def", storage["bar"])

        storage.rollbackTransaction()
        assertEquals("123", storage["foo"])
        assertEquals("abc", storage["bar"])

        val exception = assertFails { storage.commitTransaction() }
        assertEquals("no transaction", exception.message)
    }

    @Test
    fun nestedTransactions() {
        storage["foo"] = "123"
        storage["bar"] = "456"

        storage.beginTransaction()
        storage["foo"] = "456"

        storage.beginTransaction()
        assertEquals(2, storage.count("456"))
        assertEquals("456", storage["foo"])

        storage["foo"] = "789"
        assertEquals("789", storage["foo"])

        storage.rollbackTransaction()
        assertEquals("456", storage["foo"])

        storage.delete("foo")
        assertNull(storage["foo"])

        storage.rollbackTransaction()
        assertEquals("123", storage["foo"])
    }
}
