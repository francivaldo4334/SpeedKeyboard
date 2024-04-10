package br.com.fcr.speedkeyboard.utils

import br.com.fcr.speedkeyboard.R
import kotlin.math.atan2

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

    fun calcAngle(v1: Pair<Double, Double>, v2: Pair<Double, Double>): Double {
        val crossProduct = v1.first * v2.second - v1.second * v2.first
        val dotProduct = v1.first * v2.first + v1.second * v2.second
        var angle = atan2(crossProduct, dotProduct)
        angle = Math.toDegrees(angle)
        if (angle < 0)
            angle += 360
        return angle
    }

    fun getRound45(angle: Double): Double {
        val rest = (angle % 45)
        val roundedAngle = angle - rest
        return if (rest >= (45/2))
            roundedAngle + 45
        else
            roundedAngle
    }
    fun getDirectionsByRounded45(rounded:Double): List<Directions>{
        return when (rounded.toInt()){
            45*1 -> listOf(Directions.UP,Directions.RIGHT)
            45*2 -> listOf(Directions.RIGHT)
            45*3 -> listOf(Directions.DOWN,Directions.RIGHT)
            45*4 -> listOf(Directions.DOWN)
            45*5 -> listOf(Directions.DOWN,Directions.LEFT)
            45*6 -> listOf(Directions.LEFT)
            45*7 -> listOf(Directions.LEFT,Directions.UP)
            else -> listOf(Directions.UP)
        }
    }
}