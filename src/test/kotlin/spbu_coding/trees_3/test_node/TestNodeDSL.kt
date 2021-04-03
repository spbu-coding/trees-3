package spbu_coding.trees_3.test_node

import spbu_coding.trees_3.*

typealias TestNodeParent<T> = NodeParent<String, String, T>
typealias TestNode<T> = Node<String, String, T>
typealias TestRootHolder<T> = RootHolder<String, String, T>

const val ROOT_NAME = "root"
val TestNode<*>.name: String get() = value
val TestNode<*>.shortName: String get() = if (name == ROOT_NAME) "" else name

inline fun <T> createTestNode(
    balancerData: T,
    name: String,
    parent: TestNodeParent<T>? = null,
    init: TestNode<T>.() -> Unit
): TestNode<T> = TestNode(name, name, balancerData, parent = parent).apply(init)

inline fun <T> TestRootHolder<T>.root(balancerData: T, init: TestNode<T>.() -> Unit): TestNode<T> =
    createTestNode(balancerData, ROOT_NAME, this) {
        this@root.root = this
        init()
    }

inline fun <T> TestNode<T>.left(balancerData: T, init: TestNode<T>.() -> Unit): TestNode<T> =
    createTestNode(balancerData, shortName + "l", this) {
        this@left.left = this
        init()
    }

inline fun <T> TestNode<T>.right(balancerData: T, init: TestNode<T>.() -> Unit): TestNode<T> =
    createTestNode(balancerData, shortName + "r", this) {
        this@right.right = this
        init()
    }
