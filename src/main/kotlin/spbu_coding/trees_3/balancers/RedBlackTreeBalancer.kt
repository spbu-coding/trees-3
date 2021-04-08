package spbu_coding.trees_3.balancers

import spbu_coding.trees_3.*
import spbu_coding.trees_3.NodeSide.*
import spbu_coding.trees_3.balancers.NodeColor.*

enum class NodeColor {
    RED, BLACK
}

typealias RBNode<K, V> = Node<K, V, NodeColor>

object RedBlackTreeBalancer : TreeBalancer<NodeColor> {
    var RBNode<*, *>?.color: NodeColor
        get() = this?.balancerData ?: BLACK
        set(value) = run { this?.balancerData = value }

    private fun <K, V> rotate(side: NodeSide, node: RBNode<K, V>) {
        val c = node[side.opposite]!!
        val gc = c[side]
        c.attachInPlaceOf(node)
        node.attachChild(side.opposite, gc)
        c.attachChild(side, node)
    }

    override val initialNodeBalancerData: NodeColor get() = RED

    override fun <K, V> rebalanceAfterInsertion(node: RBNode<K, V>) {
        @Suppress("NAME_SHADOWING") // intentional making parameter mutable
        var node = node
        while (true) {
            var parent = node.parentNode ?: break
            if (parent.color == BLACK) break
            val parentSide = parent.side!!
            val grandparent = parent.parentNode!!
            val uncle = grandparent[parentSide.opposite]
            when (uncle.color) {
                RED -> {
                    parent.color = BLACK
                    uncle.color = BLACK
                    grandparent.color = RED
                    node = grandparent
                }
                BLACK -> {
                    if (node.side != parentSide) {
                        node = parent
                        rotate(parentSide, node)
                        parent = node.parentNode!!
                    }
                    parent.color = BLACK
                    grandparent.color = RED
                    rotate(parentSide.opposite, grandparent)
                }
            }
        }
        while (true) node = node.parentNode ?: break
        node.color = BLACK
    }

    override fun <K, V> rebalanceAfterDeletion(node: RBNode<K, V>) {
        if (node.color == RED) return
        @Suppress("NAME_SHADOWING") // intentional making parameter mutable
        var node = node.left ?: node.right ?: node
        while (node.color == BLACK) {
            val parent = node.parentNode ?: break
            val nodeSide = node.side ?: if (parent.has(LEFT)) RIGHT else LEFT
            var sib = parent[nodeSide.opposite]!!
            if (sib.color == RED) {
                sib.color = BLACK
                parent.color = RED
                rotate(nodeSide, parent)
                sib = parent[nodeSide.opposite]!!
            }
            if (sib.left.color == BLACK && sib.right.color == BLACK) {
                sib.color = RED
                node = parent
            } else {
                if (sib[nodeSide.opposite].color == BLACK) {
                    sib[nodeSide].color = BLACK
                    sib.color = RED
                    rotate(nodeSide.opposite, sib)
                    sib = parent[nodeSide.opposite]!!
                }
                sib.color = parent.color
                parent.color = BLACK
                sib[nodeSide.opposite].color = BLACK
                rotate(nodeSide, parent)
                while (true) node = node.parentNode ?: break
                break
            }
        }
        node.color = BLACK
    }
}
