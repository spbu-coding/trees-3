package spbu_coding.trees_3

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.AbstractMap.SimpleImmutableEntry

data class HashHolder(val hash: Int) {
    override fun equals(other: Any?): Boolean = other is HashHolder && hash == other.hash
    override fun hashCode(): Int = hash
}

class NodeTest {
    @Nested
    inner class Constructor {
        @Test
        fun `creating node with parent should create node with parent`() {
            val parent = RootHolder<Unit, Unit, Unit>()
            val node = Node(Unit, Unit, Unit, parent = parent)

            assertSame(parent, node.parent)
        }

        @Test
        fun `creating node without parent should create node without parent`() {
            val node = Node(Unit, Unit, Unit)

            assertThrows<UninitializedPropertyAccessException> { node.parent }
        }
    }

    @Nested
    inner class Functions {
        private val key = HashHolder(1)
        private val value = HashHolder(3)
        private lateinit var node: Node<HashHolder, HashHolder, Unit>

        @BeforeEach
        fun init() {
            node = Node(key, value, Unit)
        }

        @Test
        fun `setValue should return old value and set new value`() {
            val newValue = HashHolder(2)
            val returnedValue = node.setValue(newValue)

            assertSame(value, returnedValue)
            assertSame(newValue, node.value)
        }

        @Test
        fun `toString should return string of form key=value`() {
            val returnedString = node.toString()

            assertEquals("HashHolder(hash=1)=HashHolder(hash=3)", returnedString)
        }

        @Nested
        inner class HashCode {
            @Test
            fun `hashCode should return xor of key hash and value hash`() {
                val returnedHash = node.hashCode()

                assertEquals(2, returnedHash)
            }

            @Test
            fun `hashCode of node with null key and null value should be 0`() {
                assertEquals(0, Node(null, null, Unit).hashCode())
            }
        }

        // explicit `equals` call makes it clear that `Node#equals` is called
        @Suppress("ReplaceCallWithBinaryOperator")
        @Nested
        inner class Equals {
            @Test
            fun `node should be equal to map entry representing the same mapping`() {
                assertTrue(node.equals(SimpleImmutableEntry(HashHolder(1), HashHolder(3))))
            }

            @Test
            fun `node should not be equal to map entry representing a different mapping`() {
                assertFalse(node.equals(SimpleImmutableEntry(HashHolder(0), HashHolder(3))))
            }
        }
    }
}
