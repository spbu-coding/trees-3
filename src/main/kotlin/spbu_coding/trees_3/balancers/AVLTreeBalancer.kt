package spbu_coding.trees_3.balancers

import spbu_coding.trees_3.*

typealias AVLNode<K, V> = Node<K, V, Int>

object AVLTreeBalancer : TreeBalancer<Int> {
    private val AVLNode<*, *>?.height: Int get() = this?.balancerData ?: 0
    private val AVLNode<*, *>.balanceFactor: Int get() = right.height - left.height
    private fun AVLNode<*, *>.isHeavy(side: NodeSide): Boolean = this[side].height > this[side.opposite].height

    override val initialNodeBalancerData: Int get() = 1

    override fun <K, V> rebalanceAfterInsertion(node: AVLNode<K, V>) {
        rebalanceAfterChildHeightIncrease(node.parentNode)
    }

    override fun <K, V> rebalanceAfterDeletion(node: AVLNode<K, V>) {
        rebalanceAfterChildHeightDecrease(node.parentNode)
    }

    private fun <K, V> rebalanceAfterChildHeightIncrease(node: AVLNode<K, V>?) {
        if (node == null) return
        node.fixHeight()
        when (val balanceFactor = node.balanceFactor) {
            0 -> return
            +1, -1 -> rebalanceAfterChildHeightIncrease(node.parentNode)
            else -> rotateUnbalanced(node, NodeSide.fromSignOf(balanceFactor)!!)
        }
    }

    private fun <K, V> rebalanceAfterChildHeightDecrease(node: AVLNode<K, V>?) {
        if (node == null) return
        node.fixHeight()
        when (val balanceFactor = node.balanceFactor) {
            0 -> rebalanceAfterChildHeightDecrease(node.parentNode)
            +1, -1 -> return
            else -> {
                val oldHeight = node.height
                val newSubTreeRoot = rotateUnbalanced(node, NodeSide.fromSignOf(balanceFactor)!!)
                if (newSubTreeRoot.height != oldHeight)
                    rebalanceAfterChildHeightDecrease(newSubTreeRoot.parentNode)
            }
        }
    }

    // internal for testing
    internal fun <K, V> rotateUnbalanced(node: AVLNode<K, V>, heavySide: NodeSide): AVLNode<K, V> {
        if (node[heavySide]!!.isHeavy(heavySide.opposite)) simpleRotate(node[heavySide]!!, heavySide.opposite)
        return simpleRotate(node, heavySide)
    }

    // internal for testing
    internal fun <K, V> simpleRotate(node: AVLNode<K, V>, heavySide: NodeSide): AVLNode<K, V> {
        val lightSide = heavySide.opposite
        val heavyChild = node[heavySide]!!
        val grandChild = heavyChild[lightSide]
        heavyChild.attachInPlaceOf(node)
        node.attachChild(heavySide, grandChild)
        heavyChild.attachChild(lightSide, node)
        node.fixHeight()
        heavyChild.fixHeight()
        return heavyChild
    }

    private fun AVLNode<*, *>.fixHeight() {
        balancerData = maxOf(left.height, right.height) + 1
    }
}
