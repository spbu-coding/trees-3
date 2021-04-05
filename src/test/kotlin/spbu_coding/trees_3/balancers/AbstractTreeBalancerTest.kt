package spbu_coding.trees_3.balancers

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import spbu_coding.trees_3.*
import spbu_coding.trees_3.test_node.TestRootHolder

abstract class AbstractTreeBalancerTest<B : TreeBalancer<T>, T>(private val balancerFactory: () -> B) {
    protected lateinit var balancer: B
    protected lateinit var rootHolder: TestRootHolder<T>

    @BeforeEach
    fun init() {
        rootHolder = TestRootHolder()
        balancer = balancerFactory()
    }

    fun <K, V> rebalanceAfterInsertionAssertingOrderIsPreserved(node: Node<K, V, T>) = runAssertingOrderIsPreserved {
        rebalanceAfterInsertion(node)
    }

    fun <K, V> rebalanceAfterDeletionAssertingOrderIsPreserved(node: Node<K, V, T>) = runAssertingOrderIsPreserved {
        rebalanceAfterDeletion(node)
    }

    protected inline fun runAssertingOrderIsPreserved(action: B.() -> Unit) {
        val orderBefore = rootHolder.inOrderNodeList()
        balancer.action()
        assertEquals(orderBefore, rootHolder.inOrderNodeList())
    }
}
