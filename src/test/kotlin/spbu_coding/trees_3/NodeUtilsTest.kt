package spbu_coding.trees_3

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import spbu_coding.trees_3.test_node.SimpleTestNodeTree
import spbu_coding.trees_3.test_node.TestNode
import spbu_coding.trees_3.test_node.createTestNode
import spbu_coding.trees_3.test_node.name

class NodeUtilsTest {
    private lateinit var tree: SimpleTestNodeTree

    @BeforeEach
    fun init() {
        tree = SimpleTestNodeTree()
    }

    @Test
    fun `test parentNode`() {
        listOf(
            tree.root to null,
            tree.l to tree.root,
            tree.ll to tree.l,
            tree.lr to tree.l,
            tree.lrr to tree.lr,
            tree.r to tree.root,
            tree.rl to tree.r,
            tree.rlr to tree.rl,
        ).assertAll { (input, output) ->
            assertSame(output, input.parentNode) { input.name }
        }
    }

    @Test
    fun `test isRoot`() {
        listOf(
            tree.root to true,
            tree.l to false,
            tree.ll to false,
            tree.lr to false,
            tree.lrr to false,
            tree.r to false,
            tree.rl to false,
            tree.rlr to false,
        ).assertAll { (input, output) ->
            assertEquals(output, input.isRoot) { input.name }
        }
    }

    @Nested
    inner class Detach {
        @Test
        fun `when detach is called on root then root should be null`() {
            tree.root.detach()

            tree.rootHolder.assertRootIs(null)
        }

        @Test
        fun `when detach is called on left child then left child should be null`() {
            tree.l.detach()

            tree.root.assertLeftChildIs(null)
        }

        @Test
        fun `when detach is called on right child then right child should be null`() {
            tree.r.detach()

            tree.root.assertRightChildIs(null)
        }
    }

    @Nested
    inner class Attach {
        private lateinit var newNode: TestNode<Int>

        @BeforeEach
        fun init() {
            newNode = createTestNode(-1, "new node") {}
        }

        @Nested
        inner class InPlaceOf {
            @Test
            fun `when newNode is attached in place of root then root should be newNode`() {
                newNode.attachInPlaceOf(tree.root)

                tree.rootHolder.assertRootIs(newNode)
            }

            @Test
            fun `when newNode is attached in place of left child then left child should be newNode`() {
                newNode.attachInPlaceOf(tree.l)

                tree.root.assertLeftChildIs(newNode)
            }

            @Test
            fun `when newNode is attached in place of right child then right child should be newNode`() {
                newNode.attachInPlaceOf(tree.r)

                tree.root.assertRightChildIs(newNode)
            }
        }
    }
}
