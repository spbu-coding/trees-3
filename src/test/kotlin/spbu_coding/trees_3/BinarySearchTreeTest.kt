package spbu_coding.trees_3

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spbu_coding.trees_3.NodeSide.*
import java.util.*
import java.util.AbstractMap.SimpleImmutableEntry
import kotlin.collections.Map.Entry
import kotlin.collections.MutableMap.MutableEntry

@ExtendWith(MockKExtension::class)
class BinarySearchTreeTest {
    @MockK
    private lateinit var balancerMock: TreeBalancer<Int>

    private var Node<*, *, Int>.id
        get() = balancerData
        set(value) = run { balancerData = value }

    fun <T> MockKMatcherScope.matchSameMapping(entry: Entry<Int, String>) =
        match(object : Matcher<Node<Int, String, T>> {
            override fun toString() = entry.toString()
            override fun match(arg: Node<Int, String, T>?) =
                arg != null && arg.key == entry.key && arg.value === entry.value
        })

    @BeforeEach
    fun setUpBalancerMock() {
        clearAllMocks()

        data class Edge(val parentId: Int, val childId: Int, val childSide: NodeSide)

        fun Node<*, *, Int>.extractTreeEdges(): Set<Edge> =
            findRootHolder().inOrderNodeList().asSequence().filterNot { it.isRoot }.map {
                Edge(it.parentNode!!.id, it.id, it.side!!)
            }.toSet()

        val initId = -1
        var nextId = 0
        // `BinarySearchTree` should preserve tree structure formed by `balancerData` (ids) between calls to balancer
        // the only exceptions are clearing the tree entirely and inserting one leaf node before calling `rebalanceAfterLeafInsertion`
        val lastSeenEdges = mutableSetOf<Edge>()
        every { balancerMock.initialNodeBalancerData } returns initId
        every { balancerMock.rebalanceAfterInsertion<Int, String>(any()) } answers {
            val node = firstArg<Node<Int, String, Int>>()
            assertNull(node.left)
            assertNull(node.right)
            assertDoesNotThrow { node.parent }
            assertSame(initId, node.id)
            node.id = nextId++
            if (node.isRoot) lastSeenEdges.clear()
            else {
                lastSeenEdges.add(Edge(node.parentNode!!.id, node.id, node.side!!))
                assertEquals(lastSeenEdges, node.extractTreeEdges())
            }
        }
        every { balancerMock.rebalanceAfterDeletion<Int, String>(any()) } answers {
            val node = firstArg<Node<Int, String, Int>>()
            val parent = node.parent
            assertTrue(!node.has(LEFT) || !node.has(RIGHT))
            val child = node.left ?: node.right
            if (child != null)
                lastSeenEdges.remove(lastSeenEdges.single { it.childId == child.id })
            if (parent is Node) {
                val edgeToNode = lastSeenEdges.single { it.childId == node.id }
                lastSeenEdges.remove(edgeToNode)
                if (child != null) lastSeenEdges.add(Edge(parent.id, child.id, edgeToNode.childSide))
            }
            assertEquals(lastSeenEdges, node.extractTreeEdges())
        }
    }

    @Nested
    inner class NaturalOrderTreeTest : BaseTreeTest(
        naturalOrder(),
        { BinarySearchTree.of(balancerMock) }
    )

    @Nested
    inner class ReverseOrderTreeTest : BaseTreeTest(
        reverseOrder(),
        { BinarySearchTree.of(balancerMock, reverseOrder()) }
    )

    abstract inner class BaseTreeTest(
        private val comparator: Comparator<Int>,
        private val treeConstructor: () -> BinarySearchTree<Int, String, *>
    ) {
        private lateinit var tree: BinarySearchTree<Int, String, *>

        @BeforeEach
        fun init() {
            tree = treeConstructor()
        }

        @Nested
        inner class EmptyAfterCreation : BaseEmptyTreeTest()

        @Nested
        inner class FilledWithDigitsMappedToStringRepresentations : BaseNonEmptyTreeTest(
            digits.toStringEntries(),
            negativeDigits
        ) {
            @BeforeEach
            fun init() {
                for (digit in digits)
                    tree[digit] = digit.toString().intern()
            }

            @Nested
            inner class WithOddDigitsRemoved : BaseNonEmptyTreeTest(
                evenDigits.toStringEntries(),
                oddDigits
            ) {
                @BeforeEach
                fun init() {
                    for (oddDigit in oddDigits)
                        tree.remove(oddDigit)
                }
            }

            @Nested
            inner class WithOddDigitEntriesRemoved : BaseNonEmptyTreeTest(
                evenDigits.toStringEntries(),
                oddDigits
            ) {
                @BeforeEach
                fun init() {
                    for (entry in oddDigits.toStringEntries())
                        tree.entries.remove(entry)
                }
            }

            @Nested
            inner class WithOddDigitEntriesRemovedViaIterator : BaseNonEmptyTreeTest(
                evenDigits.toStringEntries(),
                oddDigits
            ) {
                @BeforeEach
                fun init() {
                    val iterator = tree.entries.iterator()
                    while (iterator.hasNext()) {
                        if (iterator.next().key % 2 == 1)
                            iterator.remove()
                    }
                }
            }

            @Nested
            inner class WithEvenDigitMappingsPrefixedWithStar : BaseNonEmptyTreeTest(
                digitsToStringEntriesButEvenDigitsPrefixedWithStar,
                negativeDigits
            ) {
                @BeforeEach
                fun init() {
                    for (evenDigit in evenDigits)
                        tree[evenDigit] = "*$evenDigit".intern()
                }
            }

            @Nested
            inner class WithEvenDigitMappingsPrefixedWithStarViaIterator : BaseNonEmptyTreeTest(
                digitsToStringEntriesButEvenDigitsPrefixedWithStar,
                negativeDigits
            ) {
                @BeforeEach
                fun init() {
                    val iterator = tree.entries.iterator()
                    while (iterator.hasNext()) {
                        val entry = iterator.next()
                        if (entry.key % 2 == 0)
                            entry.setValue("*${entry.key}".intern())
                    }
                }
            }
        }

        abstract inner class BaseNonEmptyTreeTest(presentEntries: Set<Entry<Int, String>>, absentKeys: Set<Int>) :
            BaseMaybeEmptyTreeTest(presentEntries, absentKeys) {
            fun presentEntries() = presentEntries.take(2)

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `tree should contain present key`(presentEntry: Entry<Int, String>) {
                assertTrue(tree.containsKey(presentEntry.key))
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `when present key is passed to get then associated value should be returned`(presentEntry: Entry<Int, String>) {
                assertSame(presentEntry.value, tree[presentEntry.key])
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `when present key is passed to put then previously associated value should be returned`(presentEntry: Entry<Int, String>) {
                assertSame(presentEntry.value, tree.put(presentEntry.key, String()))
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `when present key is passed to remove then previously associated value should be returned and balancer#rebalanceAfterDeletion should be called`(
                presentEntry: Entry<Int, String>
            ) {
                assertSame(presentEntry.value, tree.remove(presentEntry.key))
                verify { balancerMock.rebalanceAfterDeletion(matchSameMapping(presentEntry)) }
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `when present entry is passed to entries#remove then true should be returned and balancer#rebalanceAfterDeletion should be called`(
                presentEntry: Entry<Int, String>
            ) {
                assertTrue(tree.entries.remove(presentEntry))
                verify { balancerMock.rebalanceAfterDeletion(matchSameMapping(presentEntry)) }
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `when present entry with modified value is passed to entries#remove then false should be returned`(
                presentEntry: Entry<Int, String>
            ) {
                assertFalse(tree.entries.remove(SimpleImmutableEntry(presentEntry.key, String())))
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `when present entry with modified key is passed to entries#remove then false should be returned`(
                presentEntry: Entry<Int, String>
            ) {
                assertFalse(tree.entries.remove(SimpleImmutableEntry(presentEntry.key + 1, presentEntry.value)))
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(PRESENT_ENTRIES)
            fun `entry set should contain present entry`(presentEntry: Entry<Int, String>) {
                assertTrue(tree.entries.contains(presentEntry))
            }

            @Nested
            inner class HasNextNonEmptyEntryIteratorTest : BaseNonEmptyEntryIteratorTest(
                { hasNext() },
                setOf(0, presentEntries.size / 2, presentEntries.size)
            )

            @Nested
            inner class NextNonEmptyEntryIteratorTest : BaseNonEmptyEntryIteratorTest(
                { next() },
                setOf(0, presentEntries.size / 2, presentEntries.size - 1)
            )

            @Nested
            inner class RemoveNonEmptyEntryIteratorTest : BaseNonEmptyEntryIteratorTest(
                { remove() },
                setOf(1, presentEntries.size / 2, presentEntries.size)
            ) {
                @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
                @MethodSource(NEXT_CALL_COUNTS)
                fun `when remove is called two times in a row then IllegalStateException should be thrown`(nextCalls: Int) {
                    repeat(nextCalls) { iterator.next() }
                    iterator.remove()
                    assertThrows<IllegalStateException> { iterator.remove() }
                }

                @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
                @MethodSource(NEXT_CALL_COUNTS)
                fun `when remove is called then balancer#rebalanceAfterDeletion should be called`(nextCalls: Int) {
                    repeat(nextCalls - 1) { iterator.next() }
                    val lastReturnedEntry = iterator.next()
                    iterator.remove()
                    verify { balancerMock.rebalanceAfterDeletion(matchSameMapping(lastReturnedEntry)) }
                }
            }

            abstract inner class BaseNonEmptyEntryIteratorTest(
                private val testedMethod: MutableIterator<MutableEntry<Int, String>>.() -> Unit,
                private val nextCallCounts: Set<Int>
            ) : BaseEntryIteratorTest() {
                fun nextCallCounts() = nextCallCounts

                @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
                @MethodSource(NEXT_CALL_COUNTS)
                fun `when non-empty tree is concurrently cleared then entry iterator method should throw ConcurrentModificationException`(
                    nextCalls: Int
                ) {
                    repeat(nextCalls) { iterator.next() }
                    tree.clear()
                    assertThrows<ConcurrentModificationException> { iterator.testedMethod() }
                }

                @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
                @MethodSource(NEXT_CALL_COUNTS)
                fun `when absent key is concurrently added then entry iterator method should throw ConcurrentModificationException`(
                    nextCalls: Int
                ) {
                    repeat(nextCalls) { iterator.next() }
                    tree[absentKeys.first()] = String()
                    assertThrows<ConcurrentModificationException> { iterator.testedMethod() }
                }

                @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
                @MethodSource(NEXT_CALL_COUNTS)
                fun `when present mapping is changed then entry iterator method should not throw ConcurrentModificationException`(
                    nextCalls: Int
                ) {
                    repeat(nextCalls) { iterator.next() }
                    tree[presentEntries.first().key] = String()
                    assertDoesNotThrow { iterator.testedMethod() }
                }

                @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
                @MethodSource(NEXT_CALL_COUNTS)
                fun `when present key is concurrently removed then entry iterator should throw ConcurrentModificationException`(
                    nextCalls: Int
                ) {
                    repeat(nextCalls) { iterator.next() }
                    tree.remove(presentEntries.first().key)
                    assertThrows<ConcurrentModificationException> { iterator.testedMethod() }
                }

                @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
                @MethodSource(NEXT_CALL_COUNTS)
                fun `when absent mapping is concurrently removed then entry iterator should not throw ConcurrentModificationException`(
                    nextCalls: Int
                ) {
                    repeat(nextCalls) { iterator.next() }
                    tree.remove(absentKeys.first())
                    assertDoesNotThrow { iterator.testedMethod() }
                }
            }

            @Nested
            inner class EmptyAfterClearing : BaseEmptyTreeTest() {
                @BeforeEach
                fun init() {
                    tree.clear()
                }
            }

            @Nested
            inner class EmptyAfterRemoving : BaseEmptyTreeTest() {
                @BeforeEach
                fun init() {
                    for ((key, _) in this@BaseNonEmptyTreeTest.presentEntries)
                        tree.remove(key)
                }
            }

            @Nested
            inner class EmptyAfterClearingEntrySet : BaseEmptyTreeTest() {
                @BeforeEach
                fun init() {
                    tree.entries.clear()
                }
            }

            @Nested
            inner class EmptyAfterRemovingFromEntrySet : BaseEmptyTreeTest() {
                @BeforeEach
                fun init() {
                    for (entry in this@BaseNonEmptyTreeTest.presentEntries)
                        tree.entries.remove(entry)
                }
            }

            @Nested
            inner class EmptyAfterRemovingFromIterator : BaseEmptyTreeTest() {
                @BeforeEach
                fun init() {
                    val iterator = tree.entries.iterator()
                    while (iterator.hasNext()) {
                        iterator.next()
                        iterator.remove()
                    }
                }
            }
        }

        abstract inner class BaseEmptyTreeTest : BaseMaybeEmptyTreeTest(emptySet(), setOf(8, 3))

        abstract inner class BaseMaybeEmptyTreeTest(
            protected val presentEntries: Set<Entry<Int, String>>,
            protected val absentKeys: Set<Int>
        ) {
            private fun absentKeys() = absentKeys.take(2)

            @Test
            fun `size should be equal to amount of present entries`() {
                assertSame(presentEntries.size, tree.size)
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(ABSENT_KEYS)
            fun `tree should not contain absent keys`(absentKey: Int) {
                assertFalse(tree.containsKey(absentKey))
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(ABSENT_KEYS)
            fun `when absent key is passed to get then null should be returned`(absentKey: Int) {
                assertNull(tree[absentKey])
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(ABSENT_KEYS)
            fun `when absent key is passed to put then null should be returned and balancer#rebalanceAfterInsertion should be called`(
                absentKey: Int
            ) {
                val value = String()
                assertNull(tree.put(absentKey, value))
                verify {
                    balancerMock.rebalanceAfterInsertion(matchSameMapping(SimpleImmutableEntry(absentKey, value)))
                }
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(ABSENT_KEYS)
            fun `when absent key is passed to remove then null should be returned`(absentKey: Int) {
                assertNull(tree[absentKey])
            }

            @Test
            fun `entry set should be equal to present entries set`() {
                assertEquals(presentEntries, tree.entries)
            }

            @Test
            fun `when entries#add is called then UnsupportedOperationException should be thrown`() {
                assertThrows<UnsupportedOperationException> { tree.entries.add(SimpleImmutableEntry(0, String())) }
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(ABSENT_KEYS)
            fun `when absent key is passed to entries#remove then false should be returned`(absentKey: Int) {
                assertFalse(tree.entries.remove(SimpleImmutableEntry(absentKey, String())))
            }

            @ParameterizedTest(name = PARAMETRISED_TEST_NAME_INCLUDING_DISPLAY_NAME)
            @MethodSource(ABSENT_KEYS)
            fun `entry set should not contain absent entry`(absentKey: Int) {
                assertFalse(tree.entries.contains(SimpleImmutableEntry(absentKey, String())))
            }

            @Nested
            inner class MaybeEmptyEntryIteratorTest : BaseEntryIteratorTest() {
                @Test
                fun `entry iterator should yield all present entries in order determined by comparator`() {
                    val sortedPresentEntries =
                        presentEntries.sortedWith { e1, e2 -> comparator.compare(e1.key, e2.key) }
                    val entriesReturnedByIterator = tree.entries.iterator().asSequence().toList()

                    assertEquals(sortedPresentEntries, entriesReturnedByIterator)
                }

                @Test
                fun `when next is called (size+1) times entry iterator should throw NoSuchElementException`() {
                    val iterator = tree.entries.iterator()
                    assertDoesNotThrow { repeat(presentEntries.size) { iterator.next() } }
                    assertThrows<NoSuchElementException> { iterator.next() }
                }

                @Test
                fun `when remove is called before next then IllegalStateException should be thrown`() {
                    assertThrows<IllegalStateException> { iterator.remove() }
                }
            }

            abstract inner class BaseEntryIteratorTest {
                protected lateinit var iterator: MutableIterator<MutableEntry<Int, String>>

                @BeforeEach
                fun init() {
                    iterator = tree.entries.iterator()
                }
            }
        }
    }

    companion object {
        private const val PRESENT_ENTRIES = "presentEntries"
        private const val ABSENT_KEYS = "absentKeys"
        private const val NEXT_CALL_COUNTS = "nextCallCounts"

        private val digits = setOf(8, 1, 7, 6, 4, 2, 5, 0, 9, 3)
        private val evenDigits = setOf(4, 8, 0, 6, 2)
        private val oddDigits = setOf(9, 1, 3, 7, 5)
        private val negativeDigits = setOf(-4, -2, -8, -1, -3, -6, -9, -7, -5)
        private val digitsToStringEntriesButEvenDigitsPrefixedWithStar: Set<SimpleImmutableEntry<Int, String>> = setOf(
            SimpleImmutableEntry(4, "*4".intern()),
            SimpleImmutableEntry(9, "9".intern()),
            SimpleImmutableEntry(2, "*2".intern()),
            SimpleImmutableEntry(6, "*6".intern()),
            SimpleImmutableEntry(7, "7".intern()),
            SimpleImmutableEntry(0, "*0".intern()),
            SimpleImmutableEntry(5, "5".intern()),
            SimpleImmutableEntry(1, "1".intern()),
            SimpleImmutableEntry(3, "3".intern()),
            SimpleImmutableEntry(8, "*8".intern())
        )

        private fun Set<Int>.toStringEntries(): Set<SimpleImmutableEntry<Int, String>> =
            map { SimpleImmutableEntry(it, it.toString().intern()) }.toSet()
    }
}
