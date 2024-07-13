import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.example.Command
import org.example.Storage
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

@ExtendWith(MockKExtension::class)
class CommandsTest {

    @MockK
    lateinit var mockStorage: Storage

    @Test
    fun unknownCommand() {
        val exception = assertFails { Command.getInstance(listOf("NOT_EXISTING_COMMAND")) }

        assertEquals(
            "Unknown command \"NOT_EXISTING_COMMAND\". Type HELP to get a list of all supported commands",
            exception.message
        )
    }

    @Test
    fun getSuccess() {
        val command = Command.getInstance(listOf("GET", "foo"))

        every { mockStorage.get("foo") } returns "123"
        val output = command.execute(mockStorage)
        assertEquals("123", output)

        verify(exactly = 1) { mockStorage.get("foo") }
        confirmVerified(mockStorage)
    }

    @Test
    fun getKeyNotSet() {
        val command = Command.getInstance(listOf("GET", "foo"))

        every { mockStorage.get("foo") } returns null
        var output = command.execute(mockStorage)
        assertEquals("key not set", output)

        verify(exactly = 1) { mockStorage.get("foo") }
        confirmVerified(mockStorage)
    }

    // TODO: and so on...
}
