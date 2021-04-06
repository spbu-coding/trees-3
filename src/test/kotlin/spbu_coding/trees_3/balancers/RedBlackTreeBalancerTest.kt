package spbu_coding.trees_3.balancers

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import spbu_coding.trees_3.*
import spbu_coding.trees_3.balancers.NodeColor.*
import spbu_coding.trees_3.balancers.RedBlackTreeBalancer.color
import java.lang.Integer.max

typealias RBTestNode = TestNode<NodeColor>

class RedBlackTreeBalancerTest : AbstractTreeBalancerTest<RedBlackTreeBalancer, NodeColor>({ RedBlackTreeBalancer }) {

    @AfterEach
    fun assertInRBShape() {
        assertRBShapeCheckAndGetHeight(rootHolder.root)
    }

    @Test
    fun `initialNodeBalancerData should be RED`() {
        assertEquals(INIT_COLOR, balancer.initialNodeBalancerData)
    }

    @Nested
    inner class RebalanceAfterInsertion {
        @Test
        fun `when inserted node is root then tree invariants should be preserved`() {
            val inserted = root {}
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent is black then tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                inserted = left(INIT_COLOR) {}
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when uncle of inserted node is red then tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(RED) {}
                right(RED) {
                    inserted = right(INIT_COLOR) {}
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent and uncle are red nodes then tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(BLACK) {}
                right(BLACK) {
                    left(RED) {}
                    right(RED) {
                        inserted = right(INIT_COLOR) {}
                    }
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent is right node and inserted is right node then tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(BLACK) {}
                right(BLACK) {
                    right(RED) {
                        inserted = right(INIT_COLOR) {}
                    }
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent is right node and inserted is left node then tree should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(BLACK) {}
                right(BLACK) {
                    right(RED) {
                        inserted = left(INIT_COLOR) {}
                    }
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent is left node and inserted is left node then tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(BLACK) {}
                right(BLACK) {
                    left(RED) {
                        inserted = left(INIT_COLOR) {}
                    }
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when parent is left node and inserted is right node then tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(BLACK) {}
                right(BLACK) {
                    left(RED) {
                        inserted = right(INIT_COLOR) {}
                    }
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when uncle is red and grandparent exist the tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(BLACK) {
                    left(RED) {
                        inserted = left(INIT_COLOR) {}
                        right(BLACK) {}
                    }
                    right(RED) {
                        left(BLACK) {}
                        right(BLACK) {}
                    }
                }
                right(BLACK) {
                    left(BLACK) {}
                    right(BLACK) {}
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when uncle is black grandparent exist and node and parent sides are not equal the tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(BLACK) {
                    left(RED) {
                        left(BLACK) {}
                        inserted = right(INIT_COLOR) {}
                    }
                    right(BLACK) {}
                }
                right(BLACK) {
                    left(BLACK) {}
                    right(BLACK) {}
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }

        @Test
        fun `when uncle and grand grandparent are red then the tree invariants should be preserved`() {
            lateinit var inserted: RBTestNode
            root {
                left(RED) {
                    left(BLACK) {
                        left(RED) {
                            left(BLACK) {}
                            inserted = right(INIT_COLOR) {}
                        }
                        right(RED) {
                            left(BLACK) {}
                            right(BLACK) {}
                        }
                    }
                    right(BLACK) {
                        left(BLACK) {}
                        right(BLACK) {}
                    }
                }
                right(BLACK) {
                    left(BLACK) {}
                    right(BLACK) {}
                }
            }
            rebalanceAfterInsertionAssertingOrderIsPreserved(inserted)
        }
    }

    @Nested
    inner class RebalanceAfterDeletion {
        @Test
        fun `when deleted node is root then tree invariants should be preserved`() {
            val deleted = root {}
            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when deleted node is branch node then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                left(BLACK) {}
                deleted = right(BLACK) {
                    left(RED) {}
                }
            }
            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when deleted node is left red then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                deleted = left(RED) {}
            }
            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when deleted node is red then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                deleted = right(RED) {}
            }
            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when deleted node is black and sibling is red then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                deleted = left(BLACK) {}
                right(RED) {
                    left(BLACK) {}
                    right(BLACK) {}
                }
            }

            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when deleted and sibling are black and right children is red then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                deleted = left(BLACK) {
                    right(RED) {}
                }
                right(RED) {
                    left(BLACK) {}
                    right(BLACK) {}
                }
            }

            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when deleted and sibling are black and left children is red then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                deleted = left(BLACK) {
                    left(RED) {}
                }
                right(RED) {
                    left(BLACK) {}
                    right(BLACK) {}
                }
            }
            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when both sibling child's are red then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                deleted = left(BLACK) {}
                right(BLACK) {
                    left(RED) {}
                    right(RED) {}
                }
            }
            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }

        @Test
        fun `when only one sibling's child is red then tree invariants should be preserved`() {
            lateinit var deleted: RBTestNode
            root {
                left(BLACK) {
                    left(BLACK) {
                        deleted = left(BLACK) {}
                        right(BLACK) {}
                    }
                    right(BLACK) {
                        left(BLACK) {}
                        right(BLACK) {}
                    }
                }
                right(BLACK) {
                    left(RED) {
                        left(BLACK) {
                            left(BLACK) {}
                            right(BLACK) {}
                        }
                        right(BLACK) {
                            left(BLACK) {}
                            right(BLACK) {}
                        }
                    }
                    right(BLACK) {
                        left(BLACK) {}
                        right(BLACK) {}
                    }
                }
            }
            deleted.deleteNodeWithAtMostOneChild()
            rebalanceAfterDeletionAssertingOrderIsPreserved(deleted)
        }


    }

    private fun assertRBShapeCheckAndGetHeight(node: RBTestNode?): Int {
        if (node == null)
            return 1
        if (node.isRoot) {
            assertEquals(BLACK, node.color)
        }
        if (node.color == RED && !node.isRoot) {
            assertEquals(BLACK, node.parentNode?.color)
        }
        val leftSubtree = assertRBShapeCheckAndGetHeight(node.left)
        val rightSubtree = assertRBShapeCheckAndGetHeight(node.right)
        if (node.isRoot) {
            assertEquals(leftSubtree, rightSubtree)
        }
        return max(leftSubtree, rightSubtree) + if (node.color == BLACK) 1 else 0

    }

    private inline fun root(init: RBTestNode.() -> Unit): RBTestNode = rootHolder.root(BLACK, init)

    companion object {
        private val INIT_COLOR = RED
    }
}
