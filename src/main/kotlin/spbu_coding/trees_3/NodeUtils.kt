package spbu_coding.trees_3

val <K, V, T> Node<K, V, T>.parentNode: Node<K, V, T>? get() = parent as? Node
val Node<*, *, *>.isRoot: Boolean get() = parent is RootHolder
val Node<*, *, *>.isLeaf: Boolean get() = !hasLeft && !hasRight
val Node<*, *, *>.isLeft: Boolean get() = parentNode?.left === this
val Node<*, *, *>.isRight: Boolean get() = parentNode?.right === this
val Node<*, *, *>.hasLeft: Boolean get() = left != null
val Node<*, *, *>.hasRight: Boolean get() = right != null

tailrec fun <K, V, T> Node<K, V, T>.rightestDescendent(): Node<K, V, T> =
    if (hasRight) right!!.rightestDescendent()
    else this

tailrec fun <K, V, T> Node<K, V, T>.leftestDescendent(): Node<K, V, T> =
    if (hasLeft) left!!.leftestDescendent()
    else this

fun <K, V, T> Node<K, V, T>.successor(): Node<K, V, T>? {
    if (hasRight) return right!!.leftestDescendent()
    var cur = this
    while (cur.isRight) cur = cur.parentNode!!
    return cur.parentNode
}

fun <K, V, T> Node<K, V, T>.attachLeftChild(newLeft: Node<K, V, T>?) {
    left = newLeft
    newLeft?.parent = this
}

fun <K, V, T> Node<K, V, T>.attachRightChild(newRight: Node<K, V, T>?) {
    right = newRight
    newRight?.parent = this
}

fun <K, V, T> Node<K, V, T>.detach(): Unit = null.attachInPlaceOf(this)

fun <K, V, T> Node<K, V, T>?.attachInPlaceOf(other: Node<K, V, T>): Unit = other.parent.replaceChild(other, this)

fun <K, V, T> NodeParent<K, V, T>.replaceChild(old: Node<K, V, T>, new: Node<K, V, T>?) {
    when {
        this is Node && old === left -> left = new
        this is Node && old === right -> right = new
        this is RootHolder && old === root -> root = new
        else -> throw IllegalArgumentException("$old isn't child of $this")
    }
    new?.parent = this
}
