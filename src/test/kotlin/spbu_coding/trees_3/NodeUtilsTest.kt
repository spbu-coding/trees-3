package spbu_coding.trees_3

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import spbu_coding.trees_3.NodeSide.*
import spbu_coding.trees_3.test_node.SimpleTestNodeTree
import spbu_coding.trees_3.test_node.TestNode
import spbu_coding.trees_3.test_node.createTestNode
import spbu_coding.trees_3.test_node.name
import java.lang.IllegalArgumentException

class NodeUtilsTest {
    private lateinit var tree: SimpleTestNodeTree

    @BeforeEach
    fun init() {
        tree = SimpleTestNodeTree()
    }

    @Nested
    inner class NodeSideTest {

        @Test
        fun `test fromSignOf`() {
            listOf(
                -1 to LEFT,
                0 to null,
                1 to RIGHT
            ).assertAll { (input, output) ->
                assertSame(output, NodeSide.fromSignOf(input)) { input.toString() }
            }
        }

        @Test
        fun `test opposite`() {
            listOf(
                LEFT to RIGHT,
                RIGHT to LEFT
            ).assertAll { (input, output) ->
                assertEquals(output, input.opposite) { input.toString() }
            }
        }
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
            tree.rlr to tree.rl
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
            tree.rlr to false
        ).assertAll { (input, output) ->
            assertEquals(output, input.isRoot) { input.name }
        }
    }

    @Test
    fun `test isLeaf`() {
        listOf(
            tree.root to false,
            tree.l to false,
            tree.ll to true,
            tree.lr to false,
            tree.lrr to true,
            tree.r to false,
            tree.rl to false,
            tree.rlr to true
        ).assertAll { (input, output) ->
            assertEquals(output, input.isLeaf) { input.name }
        }
    }

    @Test
    fun `test side`() {
        listOf(
            tree.root to null,
            tree.l to LEFT,
            tree.ll to LEFT,
            tree.lr to RIGHT,
            tree.lrr to RIGHT,
            tree.r to RIGHT,
            tree.rl to LEFT,
            tree.rlr to RIGHT
        ).assertAll { (input, output) ->
            assertEquals(output, input.side) { input.name }
        }
    }

    @Test
    fun `test get(LEFT)`() {
        listOf(
            tree.l to tree.ll,
            tree.ll to null
        ).assertAll { (input, output) ->
            assertSame(output, input[LEFT]) { input.name }
        }
    }

    @Test
    fun `test get(RIGHT)`() {
        listOf(
            tree.l to tree.lr,
            tree.ll to null
        ).assertAll { (input, output) ->
            assertSame(output, input[RIGHT]) { input.name }
        }
    }

    @Test
    fun `test has(LEFT)`() {
        listOf(
            tree.l to true,
            tree.ll to false
        ).assertAll { (input, output) ->
            assertEquals(output, input.has(LEFT)) { input.name }
        }
    }

    @Test
    fun `test has(RIGHT)`() {
        listOf(
            tree.l to true,
            tree.ll to false
        ).assertAll { (input, output) ->
            assertEquals(output, input.has(RIGHT)) { input.name }
        }
    }

    @Test
    fun `test farthestDescendent(LEFT)`() {
        listOf(
            tree.l to tree.ll,
            tree.rlr to tree.rlr
        ).assertAll { (input, output) ->
            assertSame(output, input.farthestDescendent(LEFT)) { input.name }
        }
    }

    @Test
    fun `test farthestDescendent(RIGHT)`() {
        listOf(
            tree.l to tree.lrr,
            tree.rlr to tree.rlr
        ).assertAll { (input, output) ->
            assertSame(output, input.farthestDescendent(RIGHT)) { input.name }
        }
    }

    @Test
    fun `test nextToTheSide(RIGHT)`() {
        listOf(
            tree.root to tree.rl,
            tree.l to tree.lr,
            tree.ll to tree.l,
            tree.lr to tree.lrr,
            tree.lrr to tree.root,
            tree.r to null,
            tree.rl to tree.rlr,
            tree.rlr to tree.r
        ).assertAll { (input, output) ->
            assertSame(output, input.nextToTheSide(RIGHT)) { input.name }
        }
    }

    @Test
    fun `test nextToTheSide(LEFT)`() {
        listOf(
            tree.root to tree.lrr,
            tree.l to tree.ll,
            tree.ll to null,
            tree.lr to tree.l,
            tree.lrr to tree.lr,
            tree.r to tree.rlr,
            tree.rl to tree.root,
            tree.rlr to tree.rl
        ).assertAll { (input, output) ->
            assertSame(output, input.nextToTheSide(LEFT)) { input.name }
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
    inner class DeleteNodeWithAtMostOneChild {
        @Test
        fun `when node has only left child then node should be correctly deleted`() {
            tree.r.deleteNodeWithAtMostOneChild()

            tree.root.assertRightChildIs(tree.rl)
        }

        @Test
        fun `when node has only right child then node should be correctly deleted`() {
            tree.lr.deleteNodeWithAtMostOneChild()

            tree.l.assertRightChildIs(tree.lrr)
        }

        @Test
        fun `when node is leaf then node should be correctly deleted`() {
            tree.lrr.deleteNodeWithAtMostOneChild()

            tree.lr.assertRightChildIs(null)
        }

        @Test
        fun `when node has two children then IllegalArgumentException should be thrown`() {
            assertThrows<IllegalArgumentException> {
                tree.l.deleteNodeWithAtMostOneChild()
            }
        }

    }

    @Nested
    inner class Attach {
        private lateinit var newNode: TestNode<Int>

        @BeforeEach
        fun init() {
            newNode = createTestNode(-1, "new node") {}
        }

        @Test
        fun `when new root is attached then new root should be attached`() {
            tree.rootHolder.attachRoot(newNode)

            tree.rootHolder.assertRootIs(newNode)
        }

        @Nested
        inner class Set {
            @Test
            fun `when new left child is set then new left child should be set`() {
                tree.root[LEFT] = newNode

                assertSame(tree.root.left, newNode)
            }

            @Test
            fun `when new right child is set then new right child should be set`() {
                tree.root[RIGHT] = newNode

                assertSame(tree.root.right, newNode)
            }
        }

        @Nested
        inner class AttachChild {
            @Test
            fun `when new left child is attached then new left child should be attached`() {
                tree.root.attachChild(LEFT, newNode)

                tree.root.assertLeftChildIs(newNode)
            }

            @Test
            fun `when new right child is attached then new right child should be attached`() {
                tree.root.attachChild(RIGHT, newNode)

                tree.root.assertRightChildIs(newNode)
            }
        }

        @Nested
        inner class ReplaceChild {
            @Test
            fun `when left child is replaced with newNode then newNode should be left child`() {
                tree.root.replaceChild(tree.l, newNode)

                tree.root.assertLeftChildIs(newNode)
            }

            @Test
            fun `when right child is replaced with null then null should be right child`() {
                tree.root.replaceChild(tree.r, null)

                tree.root.assertRightChildIs(null)
            }

            @Test
            fun `when root is replaced with newNode then newNode should be root`() {
                tree.rootHolder.replaceChild(tree.root, newNode)

                tree.rootHolder.assertRootIs(newNode)
            }

            @Test
            fun `when old child is not actually node child then IllegalArgumentException`() {
                assertThrows<IllegalArgumentException> {
                    tree.root.replaceChild(tree.lrr, newNode)
                }
            }

            @Test
            fun `when old child is not actually rootHolder child then IllegalArgumentException`() {
                assertThrows<IllegalArgumentException> {
                    tree.rootHolder.replaceChild(tree.lrr, newNode)
                }
            }
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
