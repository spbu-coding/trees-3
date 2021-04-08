package spbu_coding.trees_3

import spbu_coding.trees_3.NodeSide.LEFT
import spbu_coding.trees_3.NodeSide.RIGHT
import kotlin.collections.MutableMap.MutableEntry

class BinarySearchTree<K, V, T>(
    private val balancer: TreeBalancer<T>,
    private val comparator: Comparator<K>
) : AbstractMutableMap<K, V>() {
    override var size: Int = 0
        private set(value) {
            field = value
            modCount++
        }
    override val entries: MutableSet<MutableEntry<K, V>> = EntrySet()
    private val rootHolder = RootHolder<K, V, T>()
    private var modCount = 0

    override fun containsKey(key: K): Boolean = getNode(key) != null
    override fun get(key: K): V? = getNode(key)?.value
    override fun clear() {
        rootHolder.root = null
        size = 0
    }

    override fun remove(key: K): V? {
        val node = getNode(key) ?: return null
        removeNode(node)
        return node.value
    }

    override fun put(key: K, value: V): V? {
        var cur = rootHolder.root ?: return null.also { insertLeaf(key, value) { rootHolder.attachRoot(it) } }
        while (true)
            when (val side = NodeSide.fromSignOf(comparator.compare(key, cur.key))) {
                null -> return cur.setValue(value)
                else -> cur = cur[side] ?: return null.also { insertLeaf(key, value) { cur.attachChild(side, it) } }
            }
    }

    private fun getNode(key: K): Node<K, V, T>? {
        var cur = rootHolder.root ?: return null
        while (true)
            when (val side = NodeSide.fromSignOf(comparator.compare(key, cur.key))) {
                null -> return cur
                else -> cur = cur[side] ?: return null
            }
    }

    private fun removeNode(node: Node<K, V, T>) {
        node.delete()
        balancer.rebalanceAfterDeletion(node)
        size--
    }

    private inline fun insertLeaf(key: K, value: V, leafInit: (Node<K, V, T>) -> Unit) {
        balancer.rebalanceAfterInsertion(Node(key, value, balancer.initialNodeBalancerData).also(leafInit))
        size++
    }

    private inner class EntrySet : AbstractMutableSet<MutableEntry<K, V>>() {
        override val size: Int get() = this@BinarySearchTree.size

        override fun add(element: MutableEntry<K, V>) = throw UnsupportedOperationException()
        override fun clear() = this@BinarySearchTree.clear()
        override fun iterator(): MutableIterator<MutableEntry<K, V>> = EntryIterator()
        override fun remove(element: MutableEntry<K, V>) = this@BinarySearchTree.remove(element.key, element.value)
        override fun contains(element: MutableEntry<K, V>) = getNode(element.key) == element
    }

    private inner class EntryIterator : MutableIterator<MutableEntry<K, V>> {
        private var lastReturned: Node<K, V, T>? = null
        private var next: Node<K, V, T>? = rootHolder.root?.farthestDescendent(LEFT)
        private var expectedModCount = modCount

        override fun hasNext(): Boolean {
            if (modCount != expectedModCount) throw ConcurrentModificationException()
            return next != null
        }

        override fun next(): MutableEntry<K, V> {
            if (modCount != expectedModCount) throw ConcurrentModificationException()
            val cur = next ?: throw NoSuchElementException()
            lastReturned = cur
            next = cur.nextToTheSide(RIGHT)
            return cur
        }

        override fun remove() {
            if (modCount != expectedModCount) throw ConcurrentModificationException()
            removeNode(checkNotNull(lastReturned))
            expectedModCount = modCount
            lastReturned = null
        }
    }

    companion object {
        // factory methods not requiring user to specify `T` type parameter
        fun <K, V> of(balancer: TreeBalancer<*>, comparator: Comparator<K>): BinarySearchTree<K, V, *> =
            BinarySearchTree(balancer, comparator)

        fun <K : Comparable<K>, V> of(balancer: TreeBalancer<*>): BinarySearchTree<K, V, *> =
            BinarySearchTree(balancer, naturalOrder())
    }
}
