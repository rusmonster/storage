import org.example.storage.Storage
import org.example.storage.newStorage
import org.example.storage.synchronizedStorage
import java.util.concurrent.atomic.AtomicReference
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
        val value = AtomicReference<String>()
        storage.beginTransaction()

        val thread = thread {
            value.set(storage["foo"])
        }

        Thread.sleep(1000)

        storage["foo"] = "1"
        storage.commitTransaction()

        thread.join()
        assertEquals("1", value.get())
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
        val throwable = AtomicReference<Throwable>()
        storage.beginTransaction()

        val thread = thread {
            val t = assertFails { storage.commitTransaction() }
            throwable.set(t)
        }
        thread.join()
        assertNotNull(throwable.get())
    }
}
