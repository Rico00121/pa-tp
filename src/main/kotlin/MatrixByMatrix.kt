package org.example
import kotlin.random.Random
import kotlin.system.measureNanoTime

// 全局参数：正方矩阵的大小和块数
const val MATRIX_SIZE = 10
const val NUM_BLOCKS = 2

// 使用固定种子以便复现（类似于 C 中的 srand(999)）
val random = Random(999)

// 生成一个 0~1 之间，保留 3 位小数的随机数
fun doubleRandom(): Double = random.nextInt(1000).toDouble() / 1000.0

// 生成一个 n×n 的零矩阵
fun matrixZeros(n: Int): Array<DoubleArray> {
    require(n > 0)
    return Array(n) { DoubleArray(n) }
}

// 生成一个 n×n 的随机矩阵，元素均为 doubleRandom() 的结果
fun matrixRandom(n: Int): Array<DoubleArray> = Array(n) { DoubleArray(n){ doubleRandom() } }

// 打印矩阵（仅当矩阵较小时方便观察结果）
fun matrixPrint(n: Int, A: Array<DoubleArray>) {
    require(n > 0)
    println("[")
    for (i in 0 until n) {
        println("    ${A[i].joinToString(" ") { String.format("%8.4f", it) }}")
    }
    println("]")
}

// 简单的矩阵乘法：计算 C = C + A * B
fun mbmSimple(matrixSize: Int, matrixA: Array<DoubleArray>, matrixB: Array<DoubleArray>, matrixC: Array<DoubleArray>) {
    for (i in 0 until matrixSize) {
        for (j in 0 until matrixSize) {
            for (k in 0 until matrixSize) {
                matrixC[i][j] += matrixA[i][k] * matrixB[k][j]
            }
        }
    }
}

// 对于子块（大小为 L×L）的矩阵乘法：计算
// C[cRow : cRow+L][cCol : cCol+L] = C + A[aRow : aRow+L][aCol : aCol+L] * B[bRow : bRow+L][bCol : bCol+L]
fun multiplySubMatrixBlocks(
    blockSize: Int,
    matrixA: Array<DoubleArray>, aStartRow: Int, aStartCol: Int,
    matrixB: Array<DoubleArray>, bStartRow: Int, bStartCol: Int,
    matrixC: Array<DoubleArray>, cStartRow: Int, cStartCol: Int
) {
    for (i in 0 until blockSize) {
        for (k in 0 until blockSize) {
            for (j in 0 until blockSize) {
                matrixC[cStartRow + i][cStartCol + j] += matrixA[aStartRow + i][aStartCol + k] * matrixB[bStartRow + k][bStartCol + j]
            }
        }
    }
}

// 分块矩阵乘法：将矩阵划分为 NBLOCKS×NBLOCKS 个块，每个块的尺寸为 L = N/NBLOCKS
// 计算 C = C + A * B
fun blockMatrixMultiplication(
    numBlocks: Int,
    blockSize: Int,
    matrixA: Array<DoubleArray>,
    matrixB: Array<DoubleArray>,
    matrixC: Array<DoubleArray>
) {
    require(numBlocks > 0) { "Number of blocks must be greater than 0" }
    require(blockSize > 0) { "Block size must be greater than 0" }

    for (iBlock in 0 until numBlocks) {
        for (jBlock in 0 until numBlocks) {
            for (kBlock in 0 until numBlocks) {
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


fun main() {
    println("Matrix-by-matrix multiplication (MATRIX_SIZE = $MATRIX_SIZE, NUM_BLOCKS = $NUM_BLOCKS)")
    require(MATRIX_SIZE % NUM_BLOCKS == 0) { "MATRIX_SIZE must be able to be removed by NUM_BLOCKS" }

    // 准备矩阵
    val matrixA = matrixRandom(MATRIX_SIZE)
    val matrixB = matrixRandom(MATRIX_SIZE)
    var matrixC = matrixZeros(MATRIX_SIZE)

    if (MATRIX_SIZE < 11) {
        println("A =")
        matrixPrint(MATRIX_SIZE, matrixA)
        println("B =")
        matrixPrint(MATRIX_SIZE, matrixB)
        println("C =")
        matrixPrint(MATRIX_SIZE, matrixC)
    }

    // 简单矩阵乘法
    print("Computing C = C + A*B with simple algorithm ... ")
    val timeSimple = measureNanoTime {
        mbmSimple(MATRIX_SIZE, matrixA, matrixB, matrixC)
    }
    println("done!")
    val timeSimpleSec = timeSimple / 1e9
    if (MATRIX_SIZE < 11) {
        println("C =")
        matrixPrint(MATRIX_SIZE, matrixC)
    }
    println("Clock time = $timeSimpleSec seconds")

    // 重置 C 矩阵
    matrixC = matrixZeros(MATRIX_SIZE)

    // 分块矩阵乘法
    print("Computing C = C + A*B with block algorithm ... ")
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
