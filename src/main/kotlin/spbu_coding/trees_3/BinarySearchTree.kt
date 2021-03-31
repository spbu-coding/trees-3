package spbu_coding.trees_3

import kotlin.collections.MutableMap.MutableEntry

class BinarySearchTree<K, V, T>(
    private val balancer: TreeBalancer<T>,
    private val comparator: Comparator<K>
) : AbstractMutableMap<K, V>() {
    override var size: Int = 0
        private set
    override val entries: MutableSet<MutableEntry<K, V>> = EntrySet()
    private var rootHolder = RootHolder<K, V, T>()
    private var modCount = 0

    override fun containsKey(key: K): Boolean = TODO()
    override fun get(key: K): V? = TODO()
    override fun clear() = TODO()
    override fun put(key: K, value: V): V? = TODO()
    override fun remove(key: K): V? = TODO()

    private inner class EntrySet : AbstractMutableSet<MutableEntry<K, V>>() {
        override val size: Int get() = TODO()

        override fun add(element: MutableEntry<K, V>) = TODO()
        override fun clear() = TODO()
        override fun iterator(): MutableIterator<MutableEntry<K, V>> = TODO()
        override fun remove(element: MutableEntry<K, V>) = TODO()
        override fun contains(element: MutableEntry<K, V>) = TODO()
    }

    private inner class EntryIterator : MutableIterator<MutableEntry<K, V>> {
        override fun hasNext() = TODO()
        override fun next(): MutableEntry<K, V> = TODO()
        override fun remove() = TODO()
    }

    companion object {
        // factory methods not requiring user to specify `T` type parameter
        fun <K, V> of(balancer: TreeBalancer<*>, comparator: Comparator<K>): BinarySearchTree<K, V, *> = TODO()
        fun <K : Comparable<K>, V> of(balancer: TreeBalancer<*>): BinarySearchTree<K, V, *> = TODO()
    }
}
