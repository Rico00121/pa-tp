package org.example

data class Point(val x: Double): Figure {
    override fun intersectWith(other: Figure?): Figure? {
        return when (other) {
            is Point -> if (this.x == other.x) Point(this.x) else null
            is Segment -> if (other.x <= this.x && this.x <= other.x + other.d) this else null
            else -> null
        }
    }

    override fun toString(): String {
        return "Point(" + this.x + ")"
    }
}

// main
fun main() {
    println("Point class")
    val p1 = Point(1.5)
    println(p1)
    val p2 = Point(2.0)
    println(p2)
    val p3 = p1.intersectWith(p2)
    println("The intersection is: $p3")
    println()

    val s = Segment(1.0, 2.0)
    println("Now we define: $s")
    println("And the intersection of " + p1 + " with " + s + " is " + p1.intersectWith(s))
}
