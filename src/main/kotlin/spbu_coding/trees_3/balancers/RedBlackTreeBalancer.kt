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
        set(value) {
            if (this != null) {
                balancerData = value
            }
        }

    private fun <K, V> RBNode<K, V>.sibling() = parentNode?.let { it[side!!.opposite] }

    private fun <K, V> RBNode<K, V>.uncle() = parentNode?.sibling()

    private fun <K, V> RBNode<K, V>.grandparent() = parentNode?.parentNode

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
        while (!node.isRoot && node.parentNode.color == RED) {
            val uncle = node.uncle()
            if (uncle.color == RED) {
                node.parentNode.color = BLACK
                uncle.color = BLACK
                node.grandparent().color = RED
                node = node.grandparent() ?: break
            } else {
                val side = node.parentNode?.side
                if (node.side != node.parentNode?.side) {
                    node = node.parentNode ?: break
                    rotate(side!!, node)
                }
                node.parentNode.color = BLACK
                node.grandparent().color = RED
                rotate(side?.opposite!!, node.grandparent()!!)
            }
        }
        while (!node.isRoot) node = node.parentNode!!
        node.color = BLACK
    }

    override fun <K, V> rebalanceAfterDeletion(node: RBNode<K, V>) {
        if (node.color == RED)
            return
        @Suppress("NAME_SHADOWING") // intentional making parameter mutable
        var node = node.left ?: node.right ?: node
        while (!node.isRoot && node.color == BLACK) {
            val nodeSide = node.side ?: if (node.parentNode?.has(LEFT) == true) RIGHT else LEFT
            val parent = node.parentNode ?: break
            var sib = parent[nodeSide.opposite]
            if (sib.color == RED) {
                sib.color = BLACK
                parent.color = RED
                rotate(nodeSide, node.parentNode ?: break)
                sib = parent[nodeSide.opposite]
            }
            if (sib?.left.color == BLACK && sib?.right.color == BLACK) {
                sib.color = RED
                node = parent
            } else {
                if (sib!![nodeSide.opposite].color == BLACK) {
                    sib[nodeSide].color = BLACK
                    sib.color = RED
                    rotate(nodeSide.opposite, sib)
                    sib = parent[nodeSide.opposite] ?: break
                }
                sib.color = node.parentNode.color
                parent.color = BLACK
                sib[nodeSide.opposite].color = BLACK
                rotate(nodeSide, parent)
                while (!node.isRoot) node = node.parentNode!!
                break
            }
        }
        node.color = BLACK
    }
}
