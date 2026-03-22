package dam.exer_1_4

data class Vec2(val x: Double, val y: Double): Comparable<Vec2> {

    operator fun plus(other: Vec2): Vec2 {
        return Vec2(this.x + other.x, this.y + other.y)
    }

    operator fun minus(other: Vec2): Vec2 {
        return Vec2(this.x - other.x, this.y - other.y)
    }

    operator fun times(number: Double): Vec2 {
        return Vec2(this.x * number, this.y * number)
    }

    operator fun unaryMinus(): Vec2 {
        return Vec2(-this.x, -this.y)
    }

    fun magnitude(): Double {
        return kotlin.math.sqrt(x * x + y * y)
    }

    override operator fun compareTo(other: Vec2): Int {
        val magThis = this.magnitude()
        val magOther = other.magnitude()
        return magThis.compareTo(magOther)
    }

    fun dot(other: Vec2): Double {
        return (this.x * other.x) + (this.y * other.y)
    }

    fun normalized(): Vec2 {
        val mag = magnitude()
        if (mag == 0.0) throw IllegalStateException("cannot normalize zero vector")
        return Vec2(this.x / mag, this.y / mag)
    }

    operator fun get(index: Int): Double {
        return when (index) {
            0 -> this.x
            1 -> this.y
            else -> throw IndexOutOfBoundsException("index $index out of bounds")
        }
    }
}

fun main() {
    val a = Vec2 (3.0 , 4.0)
    val b = Vec2 (1.0 , 2.0)
    println ("a = $a") // a = Vec2 (x=3.0 , y =4.0)
    println ("b = $b") // b = Vec2 (x=1.0 , y =2.0)
    println ("a + b = ${a + b}") // a + b = Vec2 (x=4.0 , y =6.0)
    println ("a - b = ${a - b}") // a - b = Vec2 (x=2.0 , y =2.0)
    println ("a * 2.0 = ${a * 2.0} ") // a * 2.0 = Vec2 (x=6.0 , y =8.0)
    println ("-a = ${ -a}") // -a = Vec2 (x= -3.0 , y= -4.0)
    println ("|a| = ${a. magnitude ()}") // |a| = 5.0
    println ("a dot b = ${a.dot(b)}") // a dot b = 11.0
    println (" norm (a) = ${a. normalized ()}") // norm (a) = Vec2 (x=0.6 , y =0.8)
    println ("a[0] = ${a [0]} ") // a[0] = 3.0
    println ("a[1] = ${a [1]} ") // a[1] = 4.0
    println ("a > b = ${a > b}") // a > b = true
    println ("a < b = ${a < b}") // a < b = false
    val vectors = listOf ( Vec2 (1.0 , 0.0) , Vec2 (3.0 , 4.0) , Vec2 (0.0 , 2.0) )
    println (" Longest = ${vectors.max()}") // Longest = Vec2 (x=3.0 , y =4.0)
    println (" Shortest = ${vectors.min()}") // Shortest = Vec2 (x=1.0 , y =0.0)
}