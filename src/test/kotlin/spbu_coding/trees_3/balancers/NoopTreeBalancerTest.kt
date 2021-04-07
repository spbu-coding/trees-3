package spbu_coding.trees_3.balancers

import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import spbu_coding.trees_3.Node

@ExtendWith(MockKExtension::class)
class NoopTreeBalancerTest : AbstractTreeBalancerTest<NoopTreeBalancer, Unit>({ NoopTreeBalancer }) {
    @MockK
    private lateinit var nodeMock: Node<Unit, Unit, Unit>

    @BeforeEach
    fun setUpMocks() {
        clearAllMocks()
    }

    @Test
    fun `initialNodeBalancerData should be Unit`() {
        assertSame(Unit, balancer.initialNodeBalancerData)
    }

    @Test
    fun `when node is passed to rebalanceAfterInsertion then no node methods should be called`() {
        rebalanceAfterInsertionAssertingOrderIsPreserved(nodeMock)
        confirmVerified(nodeMock)
    }

    @Test
    fun `when node is passed to rebalanceAfterDeletion then no node methods should be called`() {
        rebalanceAfterDeletionAssertingOrderIsPreserved(nodeMock)
        confirmVerified(nodeMock)
    }
}
