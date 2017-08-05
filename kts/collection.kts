import java.math.BigDecimal
import java.util.stream.IntStream

val Int.bg get() = BigDecimal(this)

IntStream.iterate(0){Math.pow(it+1.0,2.0).toInt() }.limit(10).toArray().forEach { println(it) }

generateSequence(1.bg to 1.bg) {
    it.second to it.first + it.second
}.take(100).forEachIndexed {
    index, pair ->
    //println("$index: ${pair.first} ")
}