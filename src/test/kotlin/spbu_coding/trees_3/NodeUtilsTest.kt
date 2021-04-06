package spbu_coding.trees_3

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import spbu_coding.trees_3.NodeSide.*

class NodeUtilsTest {
    /**
     * ```
     *        root
     *     /        \
     *    l          r
     *  /   \      /
     * ll   lr    rl
     *       \      \
     *       lrr    rlr
     * ```
     */
    private lateinit var rootHolder: TestRootHolder<Int>
    private lateinit var root: TestNode<Int>
    private lateinit var l: TestNode<Int>
    private lateinit var ll: TestNode<Int>
    private lateinit var lr: TestNode<Int>
    private lateinit var lrr: TestNode<Int>
    private lateinit var r: TestNode<Int>
    private lateinit var rl: TestNode<Int>
    private lateinit var rlr: TestNode<Int>

    @BeforeEach
    fun init() {
        var nodeId = 0
        rootHolder = TestRootHolder()
        root = rootHolder.root(nodeId++) {
            l = left(nodeId++) {
                ll = left(nodeId++) {}
                lr = right(nodeId++) {
                    lrr = right(nodeId++) {}
                }
            }
            r = right(nodeId++) {
                rl = left(nodeId++) {
                    rlr = right(nodeId++) {}
                }
            }
        }
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
            root to null,
            l to root,
            ll to l,
            lr to l,
            lrr to lr,
            r to root,
            rl to r,
            rlr to rl
        ).assertAll { (input, output) ->
            assertSame(output, input.parentNode) { input.name }
        }
    }

    @Test
    fun `test isRoot`() {
        listOf(
            root to true,
            l to false,
            ll to false,
            lr to false,
            lrr to false,
            r to false,
            rl to false,
            rlr to false
        ).assertAll { (input, output) ->
            assertEquals(output, input.isRoot) { input.name }
        }
    }

    @Test
    fun `test isLeaf`() {
        listOf(
            root to false,
            l to false,
            ll to true,
            lr to false,
            lrr to true,
            r to false,
            rl to false,
            rlr to true
        ).assertAll { (input, output) ->
            assertEquals(output, input.isLeaf) { input.name }
        }
    }

    @Test
    fun `test side`() {
        listOf(
            root to null,
            l to LEFT,
            ll to LEFT,
            lr to RIGHT,
            lrr to RIGHT,
            r to RIGHT,
            rl to LEFT,
            rlr to RIGHT
        ).assertAll { (input, output) ->
            assertEquals(output, input.side) { input.name }
        }
    }

    @Test
    fun `test get(LEFT)`() {
        listOf(
            l to ll,
            ll to null
        ).assertAll { (input, output) ->
            assertSame(output, input[LEFT]) { input.name }
        }
    }

    @Test
    fun `test get(RIGHT)`() {
        listOf(
            l to lr,
            ll to null
        ).assertAll { (input, output) ->
            assertSame(output, input[RIGHT]) { input.name }
        }
    }

    @Test
    fun `test has(LEFT)`() {
        listOf(
            l to true,
            ll to false
        ).assertAll { (input, output) ->
            assertEquals(output, input.has(LEFT)) { input.name }
        }
    }

    @Test
    fun `test has(RIGHT)`() {
        listOf(
            l to true,
            ll to false
        ).assertAll { (input, output) ->
            assertEquals(output, input.has(RIGHT)) { input.name }
        }
    }

    @Test
    fun `test farthestDescendent(LEFT)`() {
        listOf(
            l to ll,
            rlr to rlr
        ).assertAll { (input, output) ->
            assertSame(output, input.farthestDescendent(LEFT)) { input.name }
        }
    }

    @Test
    fun `test farthestDescendent(RIGHT)`() {
        listOf(
            l to lrr,
            rlr to rlr
        ).assertAll { (input, output) ->
            assertSame(output, input.farthestDescendent(RIGHT)) { input.name }
        }
    }

    @Test
    fun `test nextToTheSide(RIGHT)`() {
        listOf(
            root to rl,
            l to lr,
            ll to l,
            lr to lrr,
            lrr to root,
            r to null,
            rl to rlr,
            rlr to r
        ).assertAll { (input, output) ->
            assertSame(output, input.nextToTheSide(RIGHT)) { input.name }
        }
    }

    @Test
    fun `test nextToTheSide(LEFT)`() {
        listOf(
            root to lrr,
            l to ll,
            ll to null,
            lr to l,
            lrr to lr,
            r to rlr,
            rl to root,
            rlr to rl
        ).assertAll { (input, output) ->
            assertSame(output, input.nextToTheSide(LEFT)) { input.name }
        }
    }

    @Nested
    inner class Detach {
        @Test
        fun `when detach is called on root then root should be null`() {
            root.detach()

            rootHolder.assertRootIs(null)
        }

        @Test
        fun `when detach is called on left child then left child should be null`() {
            l.detach()

            root.assertLeftChildIs(null)
        }

        @Test
        fun `when detach is called on right child then right child should be null`() {
            r.detach()

            root.assertRightChildIs(null)
        }
    }

    @Nested
    inner class DeleteNodeWithAtMostOneChild {
        @Test
        fun `when node has only left child then node should be correctly deleted`() {
            r.deleteNodeWithAtMostOneChild()

            root.assertRightChildIs(rl)
        }

        @Test
        fun `when node has only right child then node should be correctly deleted`() {
            lr.deleteNodeWithAtMostOneChild()

            l.assertRightChildIs(lrr)
        }

        @Test
        fun `when node is leaf then node should be correctly deleted`() {
            lrr.deleteNodeWithAtMostOneChild()

            lr.assertRightChildIs(null)
        }

        @Test
        fun `when node has two children then IllegalArgumentException should be thrown`() {
            assertThrows<IllegalArgumentException> {
                l.deleteNodeWithAtMostOneChild()
            }
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `when node is parent of one then node should be correctly deleted and should not be swapped with successor`() {
            val rIdBefore = r.balancerData

            r.delete()

            root.assertRightChildIs(rl)
            assertSame(rIdBefore, r.balancerData)
            assertSame(root, r.parent)
            assertSame(rl, r.left)
            assertNull(r.right)
        }

        @Test
        fun `when node is deleted then node key and value should not be changed`() {
            listOf(root, l, ll, lr, lrr, r, rl, rlr).assertAll { node ->
                val keyBefore = node.key
                val valueBefore = node.value

                node.delete()

                assertSame(keyBefore, node.key)
                assertSame(valueBefore, node.value)
            }
        }

        @Nested
        inner class NodeIsParentOfTwo {
            @Test
            fun `when successor is right child then balancerData and position should be swapped with right child and node should be correctly deleted`() {
                val lIdBefore = l.balancerData
                val lrIdBefore = lr.balancerData

                l.delete()

                assertSame(lrIdBefore, l.balancerData)
                assertSame(lIdBefore, lr.balancerData)

                root.assertLeftChildIs(lr)
                lr.assertLeftChildIs(ll)
                lr.assertRightChildIs(lrr)
                assertSame(lr, l.parent)
                assertNull(l.left)
                assertSame(lrr, l.right)
            }

            @Test
            fun `when successor is right child descendant then balancerData and position should be swapped with the descendant and node should be correctly deleted`() {
                val rootIdBefore = root.balancerData
                val rlIdBefore = rl.balancerData

                root.delete()

                assertSame(rlIdBefore, root.balancerData)
                assertSame(rootIdBefore, rl.balancerData)

                rootHolder.assertRootIs(rl)
                rl.assertLeftChildIs(l)
                rl.assertRightChildIs(r)
                r.assertLeftChildIs(rlr)
                assertSame(r, root.parent)
                assertNull(root.left)
                assertSame(rlr, root.right)
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
            rootHolder.attachRoot(newNode)

            rootHolder.assertRootIs(newNode)
        }

        @Nested
        inner class Set {
            @Test
            fun `when new left child is set then new left child should be set`() {
                root[LEFT] = newNode

                assertSame(root.left, newNode)
            }

            @Test
            fun `when new right child is set then new right child should be set`() {
                root[RIGHT] = newNode

                assertSame(root.right, newNode)
            }
        }

        @Nested
        inner class AttachChild {
            @Test
            fun `when new left child is attached then new left child should be attached`() {
                root.attachChild(LEFT, newNode)

                root.assertLeftChildIs(newNode)
            }

            @Test
            fun `when new right child is attached then new right child should be attached`() {
                root.attachChild(RIGHT, newNode)

                root.assertRightChildIs(newNode)
            }
        }

        @Nested
        inner class ReplaceChild {
            @Test
            fun `when left child is replaced with newNode then newNode should be left child`() {
                root.replaceChild(l, newNode)

                root.assertLeftChildIs(newNode)
            }

            @Test
            fun `when right child is replaced with null then null should be right child`() {
                root.replaceChild(r, null)

                root.assertRightChildIs(null)
            }

            @Test
            fun `when root is replaced with newNode then newNode should be root`() {
                rootHolder.replaceChild(root, newNode)

                rootHolder.assertRootIs(newNode)
            }

            @Test
            fun `when old child is not actually node child then IllegalArgumentException`() {
                assertThrows<IllegalArgumentException> {
                    root.replaceChild(lrr, newNode)
                }
            }

            @Test
            fun `when old child is not actually rootHolder child then IllegalArgumentException`() {
                assertThrows<IllegalArgumentException> {
                    rootHolder.replaceChild(lrr, newNode)
                }
            }
        }

        @Nested
        inner class InPlaceOf {
            @Test
            fun `when newNode is attached in place of root then root should be newNode`() {
                newNode.attachInPlaceOf(root)

                rootHolder.assertRootIs(newNode)
            }

            @Test
            fun `when newNode is attached in place of left child then left child should be newNode`() {
                newNode.attachInPlaceOf(l)

                root.assertLeftChildIs(newNode)
            }

            @Test
            fun `when newNode is attached in place of right child then right child should be newNode`() {
                newNode.attachInPlaceOf(r)

                root.assertRightChildIs(newNode)
            }
        }
    }
}
