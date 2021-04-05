package spbu_coding.trees_3.balancers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import spbu_coding.trees_3.NodeSide.RIGHT
import spbu_coding.trees_3.assertLeftChildIs
import spbu_coding.trees_3.assertRightChildIs
import spbu_coding.trees_3.assertRootIs
import spbu_coding.trees_3.deleteNodeWithAtMostOneChild
import spbu_coding.trees_3.test_node.*

typealias AVLTestNode = TestNode<Int>

class AVLTreeBalancerTest : AbstractTreeBalancerTest<AVLTreeBalancer, Int>({ AVLTreeBalancer }) {
    private var AVLTestNode.height: Int
        get() = balancerData
        set(value) = run { balancerData = value }

    @AfterEach
    fun assertInAVLShape() {
        assertInAVLShape(rootHolder.root)
    }

    private fun assertInAVLShape(node: AVLTestNode?) {
        if (node == null) return
        assertSame(maxOf(node.left?.height ?: 0, node.right?.height ?: 0) + 1, node.height)
        assertTrue((node.left?.height ?: 0) - (node.right?.height ?: 0) in -1..1) { node.name }
        assertInAVLShape(node.left)
        assertInAVLShape(node.right)
    }

    @Test
    fun `initialNodeBalancerData should be 1`() {
        assertSame(1, balancer.initialNodeBalancerData)
    }

    @Test
    fun `when simpleRotate is called then simple rotation should be performed`() {
        lateinit var l: AVLTestNode
        lateinit var r: AVLTestNode
        lateinit var rl: AVLTestNode
        lateinit var rr: AVLTestNode
        val root = root {
            l = left {}
            r = right {
                rl = left {}
                rr = right {
                    right {}
                }
            }
        }

        runAssertingOrderIsPreserved { simpleRotate(root, RIGHT) }

        rootHolder.assertRootIs(r)
        r.assertLeftChildIs(root)
        r.assertRightChildIs(rr)
        root.assertLeftChildIs(l)
        root.assertRightChildIs(rl)
    }

    @Nested
    inner class RotateUnbalanced {
        @Test
        fun `when rotateUnbalanced is called then simple rotation should be performed`() {
            lateinit var l: AVLTestNode
            lateinit var r: AVLTestNode
            lateinit var rl: AVLTestNode
            lateinit var rr: AVLTestNode
            val root = root {
                l = left {}
                r = right {
                    rl = left {}
                    rr = right {
                        right {}
                    }
                }
            }

            runAssertingOrderIsPreserved { rotateUnbalanced(root, RIGHT) }

            rootHolder.assertRootIs(r)
            r.assertLeftChildIs(root)
            r.assertRightChildIs(rr)
            root.assertLeftChildIs(l)
            root.assertRightChildIs(rl)
        }

        @Test
        fun `when rotateUnbalanced is called on node requiring double rotation then double rotation should be perform`() {
            lateinit var l: AVLTestNode
            lateinit var r: AVLTestNode
            lateinit var rl: AVLTestNode
            lateinit var rll: AVLTestNode
            lateinit var rlr: AVLTestNode
            lateinit var rr: AVLTestNode
            val root = root {
                l = left {}
                r = right {
                    rl = left {
                        rll = left {}
                        rlr = right {}
                    }
                    rr = right {}
                }
            }

            runAssertingOrderIsPreserved { rotateUnbalanced(root, RIGHT) }

            rootHolder.assertRootIs(rl)
            rl.assertLeftChildIs(root)
            rl.assertRightChildIs(r)
            root.assertLeftChildIs(l)
            root.assertRightChildIs(rll)
            r.assertLeftChildIs(rlr)
            r.assertRightChildIs(rr)
        }
    }

    @Nested
    inner class RebalanceAfterInsertion {
        @Test
        fun `when inserted node is root then tree invariants should be preserved`() {
            val inserted = root {}

            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent balance factor is 0 after insertion then tree invariants should be preserved`() {
            val parentOfInserted = root {
                right {}
            }
            val inserted = parentOfInserted.left {}

            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent balance factor is -1 and grandparent balance factor is -2 after insertion then tree invariants should be preserved`() {
            lateinit var parentOfInserted: AVLTestNode
            root {
                parentOfInserted = left {}
            }
            val inserted = parentOfInserted.left {}

            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }
    }

    @Nested
    inner class RebalanceAfterDeletion {
        @Test
        fun `when deleted node is both leaf and root then tree invariants should be preserved`() {
            val nodeToDelete = root {}

            nodeToDelete.deleteNodeWithAtMostOneChild()

            rebalanceAfterDeletionAssertingOrderIsPreserved(nodeToDelete)
        }

        @Test
        fun `when deleted node is branch node and is root then tree invariants should be preserved`() {
            val nodeToDelete = root {
                right {}
            }

            nodeToDelete.deleteNodeWithAtMostOneChild()

            rebalanceAfterDeletionAssertingOrderIsPreserved(nodeToDelete)
        }

        @Test
        fun `when parent balance factor is +1 after deletion then tree invariants should be preserved`() {
            lateinit var nodeToDelete: AVLTestNode
            root {
                nodeToDelete = left {}
                right {}
            }

            nodeToDelete.deleteNodeWithAtMostOneChild()

            rebalanceAfterDeletionAssertingOrderIsPreserved(nodeToDelete)
        }

        @Test
        fun `when parent balance factor is 0 and grandparent balance factor is -2 after deletion then tree invariants should be preserved`() {
            lateinit var nodeToDelete: AVLTestNode
            root {
                left {
                    left {
                        left {}
                    }
                    right {
                        left {}
                        right {
                            right {}
                        }
                    }
                }
                right {
                    left {}
                    right {
                        nodeToDelete = right {}
                    }
                }
            }

            nodeToDelete.deleteNodeWithAtMostOneChild()

            rebalanceAfterDeletionAssertingOrderIsPreserved(nodeToDelete)
        }

        @Test
        fun `when parent balance factor is +2, its height decreases after rotation resulting in grandparent balance factor -2 then tree invariants should be preserved`() {
            lateinit var nodeToDelete: AVLTestNode
            root {
                left {
                    left {
                        left {}
                        right {
                            right {}
                        }
                    }
                    right {
                        right {}
                    }
                }
                right {
                    nodeToDelete = left {}
                    right {
                        left {}
                    }
                }
            }

            nodeToDelete.deleteNodeWithAtMostOneChild()

            rebalanceAfterDeletionAssertingOrderIsPreserved(nodeToDelete)
        }

        @Test
        fun `when parent balance factor is +2 and it's height does not change after rotation then tree invariants should be preserved`() {
            lateinit var nodeToDelete: AVLTestNode
            root {
                left {
                    left {
                        left {
                            left {}
                            right {}
                        }
                        right {
                            left {}
                            right {}
                        }
                    }
                    right {
                        left {
                            left {}
                            right {}
                        }
                        right {
                            left {}
                            right {}
                        }
                    }
                }
                right {
                    nodeToDelete = left {}
                    right {
                        left {}
                        right {}
                    }
                }
            }

            nodeToDelete.deleteNodeWithAtMostOneChild()

            rebalanceAfterDeletionAssertingOrderIsPreserved(nodeToDelete)
        }
    }

    private inline fun root(init: AVLTestNode.() -> Unit): AVLTestNode =
        rootHolder.root(-1, init).apply { fixHeight() }

    private inline fun AVLTestNode.left(init: AVLTestNode.() -> Unit): AVLTestNode =
        left(-1, init).apply { fixHeight() }

    private inline fun AVLTestNode.right(init: AVLTestNode.() -> Unit): AVLTestNode =
        right(-1, init).apply { fixHeight() }

    private fun AVLTestNode.fixHeight() {
        height = maxOf(left?.height ?: 0, right?.height ?: 0) + 1
    }
}
