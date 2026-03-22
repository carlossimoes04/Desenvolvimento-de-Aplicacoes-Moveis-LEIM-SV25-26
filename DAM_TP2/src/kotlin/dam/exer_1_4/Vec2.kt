package dam.exer_1_4

data class Vec2(val x: Double, val y: Double) {

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

}