package br.com.fcr.speedkeyboard

import android.content.Context

class ChordsManager(private val context: Context) {
    val regexIsShiftPair = Regex("^(.)SHIFT(.)$")
    private var mode = context.getString(R.string.mode_chars)
    private val numberChords = mapOf(
        "100000" to "1SHIFT!",
        "110000" to "2SHIFT@",
        "010000" to "3SHIFT#",
        "011000" to "4SHIFT$",
        "001000" to "5SHIFT%",
        "000100" to "6SHIFT¨",
        "000110" to "7SHIFT&",
        "000010" to "8SHIFT*",
        "000011" to "9SHIFT(",
        "000001" to "0SHIFT)",
        "100001" to "SHIFT",
        "001100" to "DELETE",
        "000111" to " ",
    )
    private val charChords = mapOf(
        "100000" to "a",
        "010000" to "e",
        "001000" to "i",
        "000100" to "s",
        "000010" to "r",
        "000001" to "n",
        "110000" to "o",
        "011000" to "u",
        "100011" to "d",
        "000011" to "m",
        "100010" to "c",
        "010100" to "t",
        "010001" to "l",
        "110001" to "p",
        "000110" to "v",
        "001010" to "g",
        "100100" to "h",
        "011100" to "b",
        "001110" to "q",
        "010010" to "x",
        "001001" to "k",
        "101000" to "f",
        "000101" to "j",
        "101010" to "z",
        "111000" to "w",
        "110010" to "y",
        "000111" to " ",
        "100001" to "SHIFT",
        "001100" to "DELETE",
        "011001" to "?SHIFT/",
        "101001" to ";SHIFT:",
        "001101" to "+SHIFT=",
        "001011" to "-SHIFT_",
        "100111" to "~SHIFT^",
        "001111" to "¨",
        "010111" to "´SHIFT`",
        "100110" to "ç",
        "010110" to ",SHIFT<",
        "010011" to ".SHIFT>",
        "110100" to "[SHIFT{",
        "101100" to "]SHIFT}",
    )

    fun setMode(mode: String) {
        this.mode = mode
    }

    fun getMode(): String {
        return mode
    }

    fun getKey(chord: String, isCapslock: Boolean = false, isShift:Boolean = false): String {
        var newKeyString =  when (mode) {
            context.getString(R.string.mode_chars) -> charChords[chord] ?: ""
            context.getString(R.string.mode_number) -> numberChords[chord] ?: ""
            else -> ""
        }
        var listCharacters: List<String> = listOf()
        val previousShift = regexIsShiftPair.matches(newKeyString)
        if (previousShift) {
            listCharacters = newKeyString.split("SHIFT")
            newKeyString = listCharacters.first()
        }
        if (isShift || isCapslock)
            newKeyString = if (previousShift) listCharacters.last() else newKeyString.uppercase()
        return newKeyString
    }

    fun getMapKeys() = when (mode) {
        context.getString(R.string.mode_chars) -> charChords
        context.getString(R.string.mode_number) -> numberChords
        else -> emptyMap()
    }

    fun containsKey(chord: String): Boolean {
        return when (mode) {
            context.getString(R.string.mode_chars) -> charChords.containsKey(chord)
            context.getString(R.string.mode_number) -> numberChords.containsKey(chord)
            else -> false
        }
    }

    fun getPreviousAndDisableKeysKeys(chord: String): List<Pair<String, String>> {
        if (Regex("[0-1]{6}").matches(chord)) {
            val indexContains = buildList<Int> {
                chord.forEachIndexed { index, c ->
                    if (c == '1') add(index)
                }
            }
            return getMapKeys().filter { binding ->
                binding.key != chord &&
                        indexContains.none { binding.key[it] == '0' }
            }.map { char ->
                var previouskey = char.key
                indexContains.forEach {
                    previouskey = previouskey.replaceRange(it, it + 1, "0")
                }
                Pair(char.key, previouskey)
            }
        }
        return emptyList()
    }

    fun getButtonIdByChord(chord: String): Int? {
        return when(chord){
            "100000" -> R.id.btn0
            "010000" -> R.id.btn1
            "001000" -> R.id.btn2
            "000100" -> R.id.btn3
            "000010" -> R.id.btn4
            "000001" -> R.id.btn5
            else -> null
        }
    }

    fun getDiacritic(diacritic: String, key: String):String {
        return when(diacritic){
            "^" -> when(key) {
                    "a" -> "â"
                    "e" -> "ê"
                    "i" -> "î"
                    "o" -> "ô"
                    "u" -> "û"
                    "A" -> "Â"
                    "E" -> "Ê"
                    "I" -> "Î"
                    "O" -> "Ô"
                    "U" -> "Û"
                    else -> ""
                }
            "~" -> when(key) {
                    "a" -> "ã"
                    "e" -> "ẽ"
                    "i" -> "ĩ"
                    "o" -> "õ"
                    "u" -> "ũ"
                    "n" -> "ñ"
                    "A" -> "Ã"
                    "E" -> "Ẽ"
                    "I" -> "Ĩ"
                    "O" -> "Õ"
                    "U" -> "Ũ"
                    "N" -> "Ñ"
                    else -> ""
                }
            "´" -> when(key) {
                    "a" -> "á"
                    "e" -> "é"
                    "i" -> "í"
                    "o" -> "ó"
                    "u" -> "ú"
                    "A" -> "Á"
                    "E" -> "É"
                    "I" -> "Í"
                    "O" -> "Ó"
                    "U" -> "Ú"
                    else -> ""
                }
            "`" -> when(key) {
                    "a" -> "à"
                    "e" -> "è"
                    "i" -> "ì"
                    "o" -> "ò"
                    "u" -> "ù"
                    "A" -> "À"
                    "E" -> "È"
                    "I" -> "Ì"
                    "O" -> "Ò"
                    "U" -> "Ù"
                    else -> ""
                }
            "¨" -> when(key) {
                    "a" -> "ä"
                    "e" -> "ë"
                    "i" -> "ï"
                    "o" -> "ö"
                    "u" -> "ü"
                    "A" -> "Ä"
                    "E" -> "Ë"
                    "I" -> "Ï"
                    "O" -> "Ö"
                    "U" -> "Ü"
                    else -> ""
                }
            else -> ""
        }
    }
}