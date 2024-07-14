import org.example.storage.Storage
import org.example.storage.newStorage
import org.example.storage.synchronizedStorage
import kotlin.concurrent.thread
import kotlin.test.*

class ConcurrentStorageTest {

    private lateinit var storage: Storage

    @BeforeTest
    fun setUp() {
        storage = Storage.synchronizedStorage(Storage.newStorage())
    }

    @Test
    fun ensureBlockingRead() {
        storage["foo"] = "0"
        var value: String? = null
        storage.beginTransaction()

        val thread = thread {
            value = storage["foo"]
        }

        Thread.sleep(1000)

        storage["foo"] = "1"
        storage.commitTransaction()

        thread.join()
        assertEquals("1", value)
    }

    @Test
    fun multiThreadSet()  {
        storage["foo"] = "0"

        val numThreads = 10
        val numIterations = 1000

        val threads = List(numThreads) {
            thread {
                repeat(numIterations) {
                    storage.beginTransaction()
                    storage["foo"] = "${storage["foo"]!!.toInt() + 1}"
                    storage.commitTransaction()
                }
            }
        }

        threads.forEach { it.join() }

        assertEquals("${numThreads * numIterations}", storage["foo"])
    }

    @Test
    fun multiThreadTransactions() {
        storage["foo"] = "0"

        val numThreads = 10
        val numIterations = 1000

        val threads = List(numThreads) {
            thread {
                repeat(numIterations) {
                    storage.beginTransaction()
                    storage.beginTransaction()
                    storage.beginTransaction()

                    storage["foo"] = "${storage["foo"]!!.toInt() + 1}"
                    storage.commitTransaction()

                    storage["foo"] = "${storage["foo"]!!.toInt() + 1}"
                    storage.rollbackTransaction()

                    storage["foo"] = "${storage["foo"]!!.toInt() + 1}"
                    storage.commitTransaction()
                }
            }
        }

        threads.forEach { it.join() }

        assertEquals("${numThreads * numIterations}", storage["foo"])
    }

    @Test
    fun closeTransactionOnDifferentThread() {
        var throwable: Throwable? = null
        storage.beginTransaction()

        val thread = thread {
            throwable = assertFails { storage.commitTransaction() }
        }
        thread.join()
        assertTrue(throwable is IllegalMonitorStateException)
    }
}
