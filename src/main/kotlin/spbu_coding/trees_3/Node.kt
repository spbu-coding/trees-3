package spbu_coding.trees_3

sealed class NodeParent<K, V, T>

class Node<K, V, T>(
    override val key: K,
    override var value: V,
    var balancerData: T,
    var parent: NodeParent<K, V, T>,
    var left: Node<K, V, T>?,
    var right: Node<K, V, T>?
) : NodeParent<K, V, T>(), MutableMap.MutableEntry<K, V> {
    override fun setValue(newValue: V): V = TODO()
    override fun toString(): String = TODO()
    override fun hashCode(): Int = TODO()
    override fun equals(other: Any?): Boolean = TODO()
}

data class RootHolder<K, V, T>(var root: Node<K, V, T>? = null) : NodeParent<K, V, T>()
