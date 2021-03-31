package spbu_coding.trees_3.balancers

import spbu_coding.trees_3.Node
import spbu_coding.trees_3.TreeBalancer

typealias AVLNode<K, V> = Node<K, V, Int>

object AVLTreeBalancer : TreeBalancer<Int> {
    override val initialNodeBalancerData: Int get() = TODO()
    override fun <K, V> rebalanceAfterInsertion(node: AVLNode<K, V>) = TODO()
    override fun <K, V> rebalanceAfterDeletion(node: AVLNode<K, V>) = TODO()
}
