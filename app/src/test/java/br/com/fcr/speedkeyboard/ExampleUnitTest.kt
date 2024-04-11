package br.com.fcr.speedkeyboard

import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val v1 = Pair(0.0, -5.0)
        val v2 = Pair(-5.0, -5.0)
        val buttonIdsManager = ButtonIdsManager()
        val angle = buttonIdsManager.calcAngle(v1, v2)
        val rounded45 = buttonIdsManager.getRound45(angle)
        println("O ângulo entre os vetores em graus é: $angle")
        println("angle: ${rounded45}")
        println("DIRS: ${buttonIdsManager.getDirectionsByRounded45(rounded45)}")
        assertEquals(4, 2 + 2)
    }
}