package org.example

import java.util.*

class SSP(n: Int) {

    // attributes
    private var target: Long = 0
    private var original: LongArray = longArrayOf()

    init {
        require(n > 0) { "SSP size cannot be non-positive" }
        require(n > 2) { "SSP size is too small" }
        val random = Random()
        this.target = 0
        this.original = LongArray(n) { (it + 1).toLong() }
        // original = {1,2,3,4,...} , target = the sum of some of them
        this.original.forEach { if (random.nextBoolean()) this.target += it }
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
        val collectedSolution = (0 until i).filter { used[it] }
            .joinToString(", ") { original[it].toString() }

        println("[$collectedSolution]")
    }

    // computing the sum of all integers
    private fun totalSum(): Long {
        return original.sum()
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
    fun showIntegers(): String = "Original set = [${original.joinToString(",")}]"

    fun showTarget() = "Target is " + this.target
}


@Throws(Exception::class)
fun main() {
    val ssp = SSP(20)
    println(ssp)
    println(ssp.showIntegers())
    println(ssp.showTarget())
    println("Running bp ... ")
    val start = System.currentTimeMillis()
    ssp.simpleBp()
    val end = System.currentTimeMillis()
    println("OK; time " + (end - start))
}

