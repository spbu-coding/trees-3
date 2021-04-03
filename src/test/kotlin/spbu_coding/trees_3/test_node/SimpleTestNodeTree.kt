package spbu_coding.trees_3.test_node

/**
 * ```
 *        root
 *     /        \
 *    l          r
 *  /   \      /
 * ll   lr    rl
 *       \      \
 *       lrr    rlr
 * ```
 */
class SimpleTestNodeTree {
    val rootHolder = TestRootHolder<Int>()
    val root: TestNode<Int>
    lateinit var l: TestNode<Int> private set
    lateinit var ll: TestNode<Int> private set
    lateinit var lr: TestNode<Int> private set
    lateinit var lrr: TestNode<Int> private set
    lateinit var r: TestNode<Int> private set
    lateinit var rl: TestNode<Int> private set
    lateinit var rlr: TestNode<Int> private set

    init {
        var nodeId = 0
        root = rootHolder.root(nodeId++) {
            l = left(nodeId++) {
                ll = left(nodeId++) {}
                lr = right(nodeId++) {
                    lrr = right(nodeId++) {}
                }
            }
            r = right(nodeId++) {
                rl = left(nodeId++) {
                    rlr = right(nodeId++) {}
                }
            }
        }
    }
}
