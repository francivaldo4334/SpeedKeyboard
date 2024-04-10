package br.com.fcr.speedkeyboard.utils

import br.com.fcr.speedkeyboard.R
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class ButtonIdsManager {
    enum class Directions {
        LEFT,
        RIGHT,
        DOWN,
        UP
    }

    val ids = listOf(
            R.id.btn0,
            R.id.btn1,
            R.id.btn2,
            R.id.btn3,
            R.id.btn4,
            R.id.btn5,
    )

    fun getNextId(id: Int, vararg directions: Directions): Int {
        var currentIndex = ids.indexOf(id)
        var newIndex: Int
        for (dir in directions) {
            when (dir) {
                Directions.LEFT -> {
                    newIndex = currentIndex - 1
                    val isUpBtns = currentIndex in 0..2
                    newIndex = if (isUpBtns) {
                        if (newIndex < 0) ids.indexOf(R.id.btn2) else newIndex
                    } else {
                        if (newIndex < 3) ids.indexOf(R.id.btn5) else newIndex
                    }
                }

                Directions.RIGHT -> {
                    newIndex = currentIndex + 1
                    val isUpBtns = currentIndex in 0..2
                    newIndex = if (isUpBtns) {
                        if (newIndex > 2) ids.indexOf(R.id.btn0) else newIndex
                    } else {
                        if (newIndex > 5) ids.indexOf(R.id.btn3) else newIndex
                    }
                }

                Directions.DOWN, Directions.UP -> {
                    val isUpBtns = currentIndex in 0..2
                    newIndex = if (isUpBtns)
                        currentIndex + 3
                    else
                        currentIndex - 3
                }
            }
            currentIndex = newIndex
        }
        return ids[currentIndex]
    }

    private fun calcScale(v1: Pair<Double, Double>, v2: Pair<Double, Double>) = v1.first * v2.first + v1.second * v2.second
    private fun calcMagnitude(v: Pair<Double, Double>) = sqrt(v.first * v.first + v.second * v.second)
    fun calcAngle(v1: Pair<Double, Double>, v2: Pair<Double, Double>): Double {
        val product = calcScale(v1, v2)
        val magnitudeV1 = calcMagnitude(v1)
        val magnitudeV2 = calcMagnitude(v2)
        val productMagnitude = magnitudeV1 * magnitudeV2
        val coseno = product / productMagnitude
        val validCoseno = max(-1.0, min(coseno, 1.0))
        val anguloRad = acos(validCoseno)
        return Math.toDegrees(anguloRad)
    }

    fun getRound45(angle: Double): Double {
        var roundedValue = angle % 360
        if (roundedValue < 0) {
            roundedValue += 360
        }
        val remainder = roundedValue % 45
        return when {
            remainder in 0.0..22.5 -> roundedValue - remainder
            remainder in 22.5..67.5 -> roundedValue + (45 - remainder)
            else -> 0.0
        }
    }
}