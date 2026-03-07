package dam.exer_3

/**
 * Generates and prints a bouncing-ball height sequence.
 *
 * The sequence starts at 100.0 meters and each bounce reaches 60% of the
 * previous height. It keeps only bounces that are at least 1.0 meter high,
 * limits the output to at most 15 values, and formats each height with
 * two decimal places.
 */
fun main(){
    // based on the samples in: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/generate-sequence.html
    val bouncesList = generateSequence(100.0) { actualHeight ->
        actualHeight * 0.6
    }
    .takeWhile { it >= 1.0 } //  keep only bounces that reach at least 1 meter high
    //.take(15) //take the first 15 qualifying bounces
    .map { String.format("%.2f", it) } // rounded to 2 decimal places
    // it was used takeWhile instead of filter because filter continues to evaluate every element of an infinite
    // sequence looking for matches, which causes an infinite loop when trying to take more elements than those
    // that exist, whereas takeWhile safely halts the sequence evaluation the moment the given condition
    // is no longer met
    // filter: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/filter.html
    // takeWhile: https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/take-while.html

    println("Bounces sequence:")
    println(bouncesList.toList())
}