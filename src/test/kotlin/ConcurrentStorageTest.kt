import org.example.storage.Storage
import org.example.storage.newStorage
import org.example.storage.synchronizedStorage
import kotlin.concurrent.thread
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConcurrentStorageTest {

    private lateinit var storage: Storage

    @BeforeTest
    fun setUp() {
        storage = Storage.synchronizedStorage(Storage.newStorage())
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
}
