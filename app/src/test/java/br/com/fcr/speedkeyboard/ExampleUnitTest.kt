package br.com.fcr.speedkeyboard

import android.icu.text.Normalizer2
import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import org.junit.Test

import org.junit.Assert.*
import java.text.Normalizer

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        val diacritic = '~'
//        val char = 'a'
//        Normalizer2.getNFCInstance().normalize("",)
//        val normalized = Normalizer.normalize("${char}${diacritic}", Normalizer.Form.NFC)
//        println(normalized)
        val v1 = Pair(0.0,-5.0)
        val v2 = Pair(-5.0,0.0)
        val buttonIdsManager = ButtonIdsManager()
        val angle = buttonIdsManager.calcAngle(v1,v2)
//        println("O ângulo entre os vetores em graus é: $angle")
//        println("angle: ${buttonIdsManager.getRound45(angle)}")
        //O ângulo entre os vetores em graus é: 90.0
        //angle: 90.0
        assertEquals(4, 2 + 2)
    }
}