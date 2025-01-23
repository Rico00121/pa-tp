package org.example

import kotlin.math.min

data class Segment(val x: Double, val d: Double): Figure {
    init {
        require(d > 0) { "The distance must be positive." }
    }
    companion object {
        fun create(x: Double, y: Double): Segment {
            if (x == y) {
                throw IllegalArgumentException("Rather use Point struct for a degenerate Segment")
            }
            return if (x < y) Segment(x, y - x) else Segment(y, x - y)
        }
    }

    override fun intersectWith(other: Figure?): Figure? {
        if (other is Point) {
            val px = other.x
            if (this.x <= px && px <= this.x + this.d) return Point(px)
        } else if (other is Segment) {
            if (this.x <= other.x) {
                if (this.x + this.d == other.x) {
                    return Point(other.x)
                } else if (this.x + this.d > other.x) {
                    return Segment(other.x, min(this.x + this.d, other.x + other.d))
                }
            } else {
                if (other.x + other.d == this.x) {
                    return Point(this.x)
                } else if (other.x + other.d > this.x) {
                    return Segment(this.x, min(other.x + other.d, this.x + this.d))
                }
            }
        }
        return null
    }


    override fun toString(): String {
        return "Segment(" + this.x + "," + (this.x + this.d) + ")"
    }
}

// main
fun main() {
    println("Segment class")
    val s1 = Segment.create(1.0, 2.0)
    println(s1)
    val s2 = Segment.create(1.5, 2.5)
    println(s2)
    val s3 = s1.intersectWith(s2)
    println("The intersection is: $s3")
    println()

    val p = Point(1.5)
    println("Now we define: $p")
    println("And the intersection of " + s1 + " with " + p + " is " + s1.intersectWith(p))
}
