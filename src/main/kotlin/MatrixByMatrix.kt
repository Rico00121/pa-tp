package org.example
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.system.measureNanoTime

// The size of the square matrix
const val MATRIX_SIZE = 4000
// The number of blocks
const val NUM_BLOCKS = 20

val random = Random(999)

// Generate a random number between 0 ~ 1 and retain 3 decimal numbers
fun doubleRandom(): Double = random.nextInt(1000).toDouble() / 1000.0

// Generate a zero matrix of N × N
fun matrixZeros(n: Int): Array<DoubleArray> {
    require(n > 0)
    return Array(n) { DoubleArray(n) }
}

// Generate a random matrix of N × N, the results of the elements are double random
fun matrixRandom(n: Int): Array<DoubleArray> = Array(n) { DoubleArray(n){ doubleRandom() } }

// Printing matrix (only when the matrix is small for easy observation results)
fun matrixPrint(n: Int, A: Array<DoubleArray>) {
    require(n > 0)
    println("[")
    for (i in 0 until n) {
        println("    ${A[i].joinToString(" ") { String.format("%8.4f", it) }}")
    }
    println("]")
}

fun multiplySubMatrixBlocks(
    blockSize: Int,
    matrixA: Array<DoubleArray>, aStartRow: Int, aStartCol: Int,
    matrixB: Array<DoubleArray>, bStartRow: Int, bStartCol: Int,
    matrixC: Array<DoubleArray>, cStartRow: Int, cStartCol: Int
) {
    // i: row, j: column, k: inner index
    for (i in 0 until blockSize) {
        for (k in 0 until blockSize) {
            for (j in 0 until blockSize) {
                matrixC[cStartRow + i][cStartCol + j] += matrixA[aStartRow + i][aStartCol + k] * matrixB[bStartRow + k][bStartCol + j]
            }
        }
    }
}

// Block matrix multiplication: divide the matrix into numBlocks × numBlocks blocks
// each block size is blockSize = MATRIX_SIZE / NUM_BLOCKS
fun blockMatrixMultiplication(
    numBlocks: Int,
    blockSize: Int,
    matrixA: Array<DoubleArray>,
    matrixB: Array<DoubleArray>,
    matrixC: Array<DoubleArray>
) {
    require(numBlocks > 0) { "Number of blocks must be greater than 0" }
    require(blockSize > 0) { "Block size must be greater than 0" }

    val availableProcessors = Runtime.getRuntime().availableProcessors()
    println("Current available number of processors: $availableProcessors")
    val executor = Executors.newFixedThreadPool(availableProcessors)
    val tasks = mutableListOf<Runnable>()

    for (iBlock in 0 until numBlocks) {
        for (jBlock in 0 until numBlocks) {
            for (kBlock in 0 until numBlocks) {
                tasks.add{
                    multiplySubMatrixBlocks(
                        blockSize,
                        matrixA, iBlock * blockSize, kBlock * blockSize,
                        matrixB, kBlock * blockSize, jBlock * blockSize,
                        matrixC, iBlock * blockSize, jBlock * blockSize
                    )
                }
            }
        }
    }

    tasks.forEach { executor.execute(it) }
    executor.shutdown()
    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
}


fun main() {
    println("Matrix-by-matrix multiplication (MATRIX_SIZE = $MATRIX_SIZE, NUM_BLOCKS = $NUM_BLOCKS)")
    require(MATRIX_SIZE % NUM_BLOCKS == 0) { "MATRIX_SIZE must be able to be removed by NUM_BLOCKS" }

    // Prepare matrix
    val matrixA = matrixRandom(MATRIX_SIZE)
    val matrixB = matrixRandom(MATRIX_SIZE)
    var matrixC = matrixZeros(MATRIX_SIZE)

    // Block matrix multiplication
    println("Computing C = C + A*B with block algorithm ... ")
    val eachSubMatrixSize = MATRIX_SIZE / NUM_BLOCKS
    val timeBlock = measureNanoTime {
        blockMatrixMultiplication(NUM_BLOCKS,eachSubMatrixSize, matrixA, matrixB, matrixC)
    }
    println("done!")
    val timeBlockSec = timeBlock / 1e9
    if (MATRIX_SIZE < 11) {
        println("C =")
        matrixPrint(MATRIX_SIZE, matrixC)
    }
    println("Clock time (including extra memory allocation and freeing) = $timeBlockSec seconds")
}
