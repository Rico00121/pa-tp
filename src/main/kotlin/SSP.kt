package org.example

import java.util.*

class SSP// constructor (easy instances)
    (n: Int) {

    // attributes
    private var target: Long = 0
    private var original: LongArray = longArrayOf()

    init {
        require(n > 0) { "SSP size cannot be non-positive" }
        require(n > 2) { "SSP size is too small" }
        val random = Random()
        this.target = 0
        this.original = LongArray(n)
        // original = {1,2,3,4,...} , target = the sum of some of them
        for (i in 0 until n) {
            original[i] = (i + 1).toLong()
            if (random.nextBoolean()) this.target += original[i]
        }
    }

    // branch-and-prune without Subset (recursive, private)
    private fun simpleBpRec(i: Int, usedSum: Long, remainingSum: Long, used: BooleanArray) {
        // are we out of bounds yet? -> prune
        if (usedSum + remainingSum < this.target || usedSum > this.target) return
        // did we find a new solution already?
        if (usedSum == this.target) {
            // we simply print the solution
            printSolution(i, used)
            return
        }
        // did we use all integers already? -> Prevent array out of index
        if (i == this.original.size) return
        val updatedRemainingSum = remainingSum - this.original[i]
        // recursive call without original[i]
        used[i] = false
        this.simpleBpRec(i + 1, usedSum, updatedRemainingSum, used)
        // recursive call with original[i]
        used[i] = true
        //
        val updatedUsedSum = usedSum + this.original[i]
        this.simpleBpRec(i + 1, updatedUsedSum, updatedRemainingSum, used)
        used[i] = false // branch-and-prune without Subset
    }

    private fun printSolution(i: Int, used: BooleanArray) {
        print("[")
        var first = true
        for (k in 0 until i) {
            if (used[k]) {
                if (!first) print(",")
                print(this.original[k])
                first = false
            }
        }
        println("]")
    }

    // computing the sum of all integers
    private fun totalSum(): Long {
        val n = original.size
        var sum = original[0]
        for (i in 1 until n) sum += original[i]
        return sum
    }

    fun simpleBp() {
        val n = this.original.size
        val used = BooleanArray(n)
        val originalSum = this.totalSum()
        this.simpleBpRec(0, 0L, originalSum, used)
    }

    override fun toString(): String {
        return "SSP(n = " + original.size + "; target = " + this.target + ")"
    }


    // showing the integers in the original set
    fun showIntegers(): String {
        var s = "Original set = ["
        for (i in original.indices) {
            s += original[i]
            if (i + 1 < original.size) s = "$s,"
        }
        return "$s]"
    }

    fun showTarget(): String = "Target is " + this.target

}


@Throws(Exception::class)
fun main() {
    val ssp: SSP = SSP(20)
    println(ssp)
    println(ssp.showIntegers())
    println(ssp.showTarget())
    println("Running bp ... ")
    val start = System.currentTimeMillis()
    ssp.simpleBp()
    val end = System.currentTimeMillis()
    println("OK; time " + (end - start))
}
