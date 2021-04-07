package spbu_coding.trees_3

interface TreeBalancer<T> {
    val initialNodeBalancerData: T
    fun <K, V> rebalanceAfterInsertion(node: Node<K, V, T>)
    fun <K, V> rebalanceAfterDeletion(node: Node<K, V, T>)
}
