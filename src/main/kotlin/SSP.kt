package org.example

import java.io.File
import java.io.FileNotFoundException
import java.util.*

class SSP{

    // attributes
    private var target: Long = 0
    private var original: LongArray = longArrayOf()

     constructor(n: Int){
        require(n > 0) { "SSP size cannot be non-positive" }
        require(n > 2) { "SSP size is too small" }
        val random = Random()
        this.target = 0
        this.original = LongArray(n) { (it + 1).toLong() }
        // original = {1,2,3,4,...} , target = the sum of some of them
        this.original.forEach { if (random.nextBoolean()) this.target += it }
    }

    // constructor (from text file)
    @Throws(IllegalArgumentException::class, FileNotFoundException::class)
    constructor(filename: String?) {
        requireNotNull(filename) { "Given path/file is null" }
        val input = File(filename)
        require(input.exists()) { "Given path/file does not exist" }
        val scan = Scanner(input)

        // number of elements in the original set
        if (!scan.hasNext()) throw IllegalArgumentException("Error while parsing input file")
        val size = scan.nextInt()
        if (size <= 0) throw IllegalArgumentException("Error while parsing input file")
        this.original = LongArray(size)

        // target
        if (!scan.hasNext()) throw IllegalArgumentException("Error while parsing input file")
        this.target = scan.nextLong()

        // the original set
        for (i in 0 until size) {
            if (!scan.hasNext()) throw IllegalArgumentException("Error while parsing input file")
            original[i] = scan.nextLong()
        }
        scan.close()
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
        // recursive calls for all possible selections
        for (j in i until this.original.size) {
            val updatedRemainingSum = remainingSum - this.original[j]
            // recursive call with original[j]
            used[j] = true // marked as selected
            val updatedUsedSum = usedSum + this.original[j]
            this.simpleBpRec(j + 1, updatedUsedSum, updatedRemainingSum, used)
            used[j] = false // backtrack, marked as not selected
        }
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
        this.simpleBpRec(i = 0, usedSum = 0L, remainingSum = originalSum, used = used)
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
//    val ssp = SSP("/Users/ruikang.tao/Projects/Rennes1/pa-tp/src/main/kotlin/inst_n25.txt");
    val ssp = SSP(10)
    println(ssp)
    println(ssp.showIntegers())
    println(ssp.showTarget())
    println("Running bp ... ")
    val start = System.currentTimeMillis()
    ssp.simpleBp()
    val end = System.currentTimeMillis()
    println("OK; time " + (end - start))
}

