package spbu_coding.trees_3

sealed class NodeParent<K, V, T>

class Node<K, V, T>(
    override val key: K,
    override var value: V,
    var balancerData: T,
    var left: Node<K, V, T>? = null,
    var right: Node<K, V, T>? = null,
    parent: NodeParent<K, V, T>? = null
) : NodeParent<K, V, T>(), MutableMap.MutableEntry<K, V> {
    lateinit var parent: NodeParent<K, V, T>

    init {
        if (parent != null) this.parent = parent
    }

    override fun setValue(newValue: V): V = value.also { value = newValue }
    override fun toString() = "$key=$value"
    override fun hashCode(): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)
    override fun equals(other: Any?): Boolean = other is Map.Entry<*, *> && key == other.key && value == other.value
}

data class RootHolder<K, V, T>(var root: Node<K, V, T>? = null) : NodeParent<K, V, T>()
