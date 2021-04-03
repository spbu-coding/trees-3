package spbu_coding.trees_3

import spbu_coding.trees_3.NodeSide.LEFT
import spbu_coding.trees_3.NodeSide.RIGHT

enum class NodeSide {
    LEFT, RIGHT;

    val opposite: NodeSide
        get() = when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
        }

    companion object {
        fun fromSignOf(signed: Int): NodeSide? = when {
            signed < 0 -> LEFT
            signed > 0 -> RIGHT
            else -> null
        }
    }
}

val <K, V, T> Node<K, V, T>.parentNode: Node<K, V, T>? get() = parent as? Node
val Node<*, *, *>.isRoot: Boolean get() = parent is RootHolder
val Node<*, *, *>.isLeaf: Boolean get() = !has(LEFT) && !has(RIGHT)

val Node<*, *, *>.side: NodeSide?
    get() = when {
        parentNode?.left === this -> LEFT
        parentNode?.right === this -> RIGHT
        else -> null
    }

operator fun <K, V, T> Node<K, V, T>.set(side: NodeSide, child: Node<K, V, T>?) = when (side) {
    LEFT -> left = child
    RIGHT -> right = child
}

operator fun <K, V, T> Node<K, V, T>.get(side: NodeSide): Node<K, V, T>? = when (side) {
    LEFT -> left
    RIGHT -> right
}

fun Node<*, *, *>.has(side: NodeSide) = this[side] != null

tailrec fun <K, V, T> Node<K, V, T>.farthestDescendent(side: NodeSide): Node<K, V, T> =
    if (has(side)) this[side]!!.farthestDescendent(side)
    else this

fun <K, V, T> Node<K, V, T>.nextToTheSide(side: NodeSide): Node<K, V, T>? {
    if (has(side)) return this[side]!!.farthestDescendent(side.opposite)
    var cur = this
    while (cur.side == side) cur = cur.parentNode!!
    return cur.parentNode
}

fun <K, V, T> RootHolder<K, V, T>.attachRoot(newRoot: Node<K, V, T>?) {
    root = newRoot
    newRoot?.parent = this
}

fun <K, V, T> Node<K, V, T>.attachChild(side: NodeSide, newChild: Node<K, V, T>?) {
    this[side] = newChild
    newChild?.parent = this
}

fun <K, V, T> Node<K, V, T>.detach(): Unit = null.attachInPlaceOf(this)

fun <K, V, T> Node<K, V, T>?.attachInPlaceOf(other: Node<K, V, T>): Unit = other.parent.replaceChild(other, this)

fun <K, V, T> NodeParent<K, V, T>.replaceChild(old: Node<K, V, T>, new: Node<K, V, T>?) {
    when {
        this is Node && old === left -> left = new
        this is Node && old === right -> right = new
        this is RootHolder && old === root -> root = new
        else -> throw IllegalArgumentException("<$old> isn't child of <$this>")
    }
    new?.parent = this
}
