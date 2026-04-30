package dam.exer_1_4

/**
 * Representa um vetor matemático 2D com componentes de vírgula flutuante.
 * Implementa a interface [Comparable] para permitir a comparação de vetores
 * com base na sua magnitude (comprimento)
 *
 * Como é uma `data class`, os métodos `equals()`, `hashCode()`, `toString()`,
 * `copy()`, bem como `component1()` e `component2()` (para destructuring)
 * são gerados automaticamente pelo Kotlin
 *
 * @property x A coordenada X do vetor
 * @property y A coordenada Y do vetor
 */
data class Vec2(val x: Double, val y: Double): Comparable<Vec2> {

    /**
     * Adiciona este vetor a outro vetor
     * Suporta o operador `+`
     *
     * @param other O vetor a ser adicionado
     * @return Um novo vetor resultante da soma componente a componente
     */
    operator fun plus(other: Vec2): Vec2 {
        return Vec2(this.x + other.x, this.y + other.y)
    }

    /**
     * Subtrai outro vetor deste vetor
     * Suporta o operador `-`
     *
     * @param other O vetor a ser subtraído
     * @return Um novo vetor resultante da subtração componente a componente
     */
    operator fun minus(other: Vec2): Vec2 {
        return Vec2(this.x - other.x, this.y - other.y)
    }

    /**
     * Multiplica o vetor por um valor escalar
     * Suporta o operador `*` (ex: `v * 2.0`)
     *
     * @param number O valor escalar pelo qual o vetor será multiplicado
     * @return Um novo vetor escalado
     */
    operator fun times(number: Double): Vec2 {
        return Vec2(this.x * number, this.y * number)
    }

    /**
     * Inverte a direção do vetor
     * Suporta o operador unário `-`
     *
     * @return Um novo vetor com os componentes x e y negados
     */
    operator fun unaryMinus(): Vec2 {
        return Vec2(-this.x, -this.y)
    }

    /**
     * Calcula a norma de um vetor
     *
     * @return A norma do vetor
     */
    fun magnitude(): Double {
        return kotlin.math.sqrt(x * x + y * y)
    }

    /**
     * Compara a norma deste vetor com a de outro vetor
     * Permite o uso dos operadores relacionais `>`, `<`, `>=`, `<=`
     *
     * @param other O vetor com o qual a norma será comparada
     * @return Zero se as normas forem iguais, um número negativo
     * se a norma for menor, ou um número positivo se for maior
     */
    override operator fun compareTo(other: Vec2): Int { // foi necessário adicionar o Comparable<Vec2> na declaração da classe
        val magThis = this.magnitude()
        val magOther = other.magnitude()
        return magThis.compareTo(magOther)
    }

    /**
     * Calcula o produto escalar (dot product) entre este vetor e outro
     *
     * @param other O vetor para calcular o produto escalar
     * @return O valor do produto escalar
     */
    fun dot(other: Vec2): Double {
        return (this.x * other.x) + (this.y * other.y)
    }

    /**
     * Devolve uma versão normalizada (vetor unitário) deste vetor,
     * ou seja, um vetor com a mesma direção mas com a norma igual a 1
     *
     * @return Um novo vetor normalizado
     * @throws IllegalStateException Se o vetor for o vetor nulo (norma igual a zero)
     */
    fun normalized(): Vec2 {
        val mag = magnitude()
        if (mag == 0.0) throw IllegalStateException("cannot normalize zero vector")
        return Vec2(this.x / mag, this.y / mag)
    }

    /**
     * Acede aos componentes do vetor através de indexação
     * Suporta a sintaxe de parênteses retos (ex: `v[0]` e `v[1]`)
     *
     * @param index O índice do componente (0 para X, 1 para Y)
     * @return O valor do componente correspondente
     * @throws IndexOutOfBoundsException Se o índice não for 0 ou 1
     */
    operator fun get(index: Int): Double {
        return when (index) {
            0 -> this.x
            1 -> this.y
            else -> throw IndexOutOfBoundsException("index $index out of bounds")
        }
    }

    // component1() e component2() são gerados automaticamente
    // pela data class, permitindo: val (x, y) = a
}

/**
 * Extensão para suportar a multiplicação escalar à esquerda
 * Suporta o operador `*` quando o escalar está à esquerda
 * * Como o lado esquerdo da operação determina a classe de origem,
 * esta função tem de ser uma extensão de [Double]
 *
 * @param other O vetor a ser multiplicado
 * @return Um novo vetor escalado
 */
operator fun Double.times(other: Vec2): Vec2 {
    return Vec2(this * other.x, this * other.y)
}

fun main() {
    val a = Vec2 (3.0 , 4.0)
    val b = Vec2 (1.0 , 2.0)
    println("a = $a") // a = Vec2 (x=3.0 , y =4.0)
    println("b = $b") // b = Vec2 (x=1.0 , y =2.0)
    println("a + b = ${a + b}") // a + b = Vec2 (x=4.0 , y =6.0)
    println("a - b = ${a - b}") // a - b = Vec2 (x=2.0 , y =2.0)
    println("a * 2.0 = ${a * 2.0} ") // a * 2.0 = Vec2 (x=6.0 , y =8.0)
    println("-a = ${ -a}") // -a = Vec2 (x= -3.0 , y= -4.0)
    println("|a| = ${a. magnitude ()}") // |a| = 5.0
    println("a dot b = ${a.dot(b)}") // a dot b = 11.0
    println(" norm (a) = ${a. normalized ()}") // norm (a) = Vec2 (x=0.6 , y =0.8)
    println("a[0] = ${a [0]} ") // a[0] = 3.0
    println("a[1] = ${a [1]} ") // a[1] = 4.0
    println("a > b = ${a > b}") // a > b = true
    println("a < b = ${a < b}") // a < b = false
    val vectors = listOf ( Vec2 (1.0 , 0.0) , Vec2 (3.0 , 4.0) , Vec2 (0.0 , 2.0) )
    println(" Longest = ${vectors.max()}") // Longest = Vec2 (x=3.0 , y =4.0)
    println(" Shortest = ${vectors.min()}") // Shortest = Vec2 (x=1.0 , y =0.0)

    println("2.0 * a = ${2.0 * a}")

    val (x, y) = a
    println("x = $x")
    println("y = $y")
}