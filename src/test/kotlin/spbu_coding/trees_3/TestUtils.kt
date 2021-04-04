package spbu_coding.trees_3

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.function.Executable
import spbu_coding.trees_3.NodeSide.*

fun <K, V, T> RootHolder<K, V, T>.inOrderNodeList(): List<Node<K, V, T>> =
    generateSequence(root?.farthestDescendent(LEFT)) { it.nextToTheSide(RIGHT) }.toList()

inline fun <T> Iterable<T>.assertAll(crossinline asserter: (T) -> Unit) = assertAll(map { Executable { asserter(it) } })

fun <K, V, T> Node<K, V, T>.assertLeftChildIs(expectedLeftChild: Node<K, V, T>?) {
    assertSame(expectedLeftChild, left)
    if (expectedLeftChild != null) assertSame(this, expectedLeftChild.parent)
}

fun <K, V, T> Node<K, V, T>.assertRightChildIs(expectedRightChild: Node<K, V, T>?) {
    assertSame(expectedRightChild, right)
    if (expectedRightChild != null) assertSame(this, expectedRightChild.parent)
}

fun <K, V, T> RootHolder<K, V, T>.assertRootIs(expectedRoot: Node<K, V, T>?) {
    assertSame(expectedRoot, root)
    if (expectedRoot != null) assertSame(this, expectedRoot.parent)
}
