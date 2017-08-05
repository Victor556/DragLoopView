import kotlin.coroutines.experimental.buildIterator
import kotlin.coroutines.experimental.buildSequence

val array = 1..1000

// create a sequence with a function, returning an iterator
val sequence1 = Sequence { array.iterator() }
//println(sequence1.joinToString()) // 1, 2, 3
//println(sequence1.drop(1).joinToString()) // 2, 3
print(array.takeWhile {  it % 5 == 0 })

// create a sequence from an existing iterator
// can be iterated only once
val sequence2 = array.iterator().asSequence()
//println(sequence2.joinToString()) // 1, 2, 3
// sequence2.drop(1).joinToString() // <- iterating sequence second time will fail