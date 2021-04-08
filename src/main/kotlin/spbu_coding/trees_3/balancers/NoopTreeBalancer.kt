package spbu_coding.trees_3.balancers

import spbu_coding.trees_3.Node
import spbu_coding.trees_3.TreeBalancer

object NoopTreeBalancer : TreeBalancer<Unit> {
    override val initialNodeBalancerData: Unit get() = Unit
    override fun <K, V> rebalanceAfterInsertion(node: Node<K, V, Unit>) {
        /* no-op */
    }

    override fun <K, V> rebalanceAfterDeletion(node: Node<K, V, Unit>) {
        /* no-op */
    }
}
