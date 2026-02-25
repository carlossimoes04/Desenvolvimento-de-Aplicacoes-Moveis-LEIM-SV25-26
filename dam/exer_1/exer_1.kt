package dam.exer_1

fun main() {
    // first approach, using IntArray
    val squaredNumsA = IntArray(50) { num ->
        val aux = num + 1
        aux * aux
    }

    println("exercise 1 a)")
    for (number in squaredNumsA) println("perfect square = $number")
    println("----------------")

    // second approach, using a range (1..50) and map()
    val squaredNumsB = (1..50).map { num ->
        num * num
    }.toIntArray() // toIntArray() is necessary because map() returns a list and it's supposed to be an integer array

    println("exercise 1 b)")
    for (number in squaredNumsB) println("perfect square = $number")
    println("----------------")

    // third approach, using an array
    val squaredNumsC = Array(50) { num ->
        val aux = num + 1
        aux * aux
    }

    println("exercise 1 c)")
    for (number in squaredNumsC) println("perfect square = $number")
}