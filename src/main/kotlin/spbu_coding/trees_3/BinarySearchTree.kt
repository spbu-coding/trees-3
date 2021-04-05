package spbu_coding.trees_3

import spbu_coding.trees_3.NodeSide.LEFT
import spbu_coding.trees_3.NodeSide.RIGHT
import kotlin.collections.MutableMap.MutableEntry

class BinarySearchTree<K, V, T>(private val balancer: TreeBalancer<T>, private val comparator: Comparator<K>) :
    AbstractMutableMap<K, V>() {

    override var size: Int = 0
    override val entries: MutableSet<MutableEntry<K, V>> = EntrySet()
    var rootHolder = RootHolder<K, V, T>()
    var modCount = 0

    override fun containsKey(key: K): Boolean {
        return getNode(key) != null
    }

    override fun get(key: K): V? {
        return getNode(key)?.value
    }

    override fun clear() {
        if (rootHolder.root != null) {
            rootHolder.root = null
            modCount++
            size = 0
        }
    }

    override fun remove(key: K): V? {
        val node = getNode(key)
        if (node == null) return null
        removeNode(node)
        return node.value
    }

    override fun put(key: K, value: V): V? {
        if (rootHolder.root == null) {
            val node = Node(key, value, balancer.initialNodeBalancerData)
            rootHolder.attachRoot(node)
            balancer.rebalanceAfterInsertion(node)
            modCount++
            size++
            return null
        }
        var cur = rootHolder.root!!
        while (true) {
            if (comparator.compare(key, cur.key) < 0) {
                if (cur.left == null) {
                    val node = Node(key, value, balancer.initialNodeBalancerData)
                    cur.attachChild(LEFT, node)
                    balancer.rebalanceAfterInsertion(node)
                    modCount++
                    size++
                    return null
                }
                cur = cur.left!!
            } else if (comparator.compare(key, cur.key) > 0) {
                if (cur.right == null) {
                    val node = Node(key, value, balancer.initialNodeBalancerData)
                    cur.attachChild(RIGHT, node)
                    balancer.rebalanceAfterInsertion(node)
                    modCount++
                    size++
                    return null
                }
                cur = cur.right!!
            } else {
                val oldValue = cur.value
                cur.value = value
                return oldValue
            }
        }
    }

    fun getNode(key: K): Node<K, V, T>? {
        if (rootHolder.root == null) return null
        var cur = rootHolder.root!!
        while (true) {
            if (comparator.compare(key, cur.key) < 0) {
                if (cur.left == null) return null
                cur = cur.left!!
            } else if (comparator.compare(key, cur.key) > 0) {
                if (cur.right == null) return null
                cur = cur.right!!
            } else return cur
        }
    }

    fun removeNode(node: Node<K, V, T>) {
        ifParentOfTwoSwapNonUserDataWithSuccessor(node)
        node.deleteNodeWithAtMostOneChild()
        balancer.rebalanceAfterDeletion(node)
        modCount++
        size--
    }

    private inner class EntrySet : AbstractMutableSet<MutableEntry<K, V>>() {
        override val size: Int = this@BinarySearchTree.size

        override fun add(element: MutableEntry<K, V>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun clear() {
            this@BinarySearchTree.clear()
        }

        override fun iterator(): MutableIterator<MutableEntry<K, V>> {
            return EntryIterator()
        }

        override fun remove(element: MutableEntry<K, V>): Boolean {
            return this@BinarySearchTree.remove(element.key, element.value)
        }

        override fun contains(element: MutableEntry<K, V>): Boolean {
            return getNode(element.key) == element
        }
    }

    private inner class EntryIterator : MutableIterator<MutableEntry<K, V>> {
        override fun hasNext() = TODO()
        override fun next(): MutableEntry<K, V> = TODO()
        override fun remove() = TODO()
    }

    companion object {
        // factory methods not requiring user to specify `T` type parameter
        fun <K, V> of(balancer: TreeBalancer<*>, comparator: Comparator<K>): BinarySearchTree<K, V, *> =
            BinarySearchTree(balancer, comparator)

        fun <K : Comparable<K>, V> of(balancer: TreeBalancer<*>): BinarySearchTree<K, V, *> =
            BinarySearchTree(balancer, naturalOrder())

        fun <K, V, T> ifParentOfTwoSwapNonUserDataWithSuccessor(node: Node<K, V, T>) {
            TODO()
        }
    }
}
