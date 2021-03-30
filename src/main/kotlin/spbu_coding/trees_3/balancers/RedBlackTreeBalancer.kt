package spbu_coding.trees_3.balancers

import spbu_coding.trees_3.Node
import spbu_coding.trees_3.TreeBalancer

enum class NodeColor {
    RED, BLACK
}

typealias RBNode<K, V> = Node<K, V, NodeColor>

object RedBlackTreeBalancer : TreeBalancer<NodeColor> {
    override val initialNodeBalancerData: NodeColor get() = TODO()
    override fun <K, V> rebalanceAfterInsertion(node: RBNode<K, V>) = TODO()
    override fun <K, V> rebalanceAfterDeletion(node: RBNode<K, V>) = TODO()
}
