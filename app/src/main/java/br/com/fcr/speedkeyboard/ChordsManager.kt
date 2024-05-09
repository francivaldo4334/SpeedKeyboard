package br.com.fcr.speedkeyboard

import android.content.Context

class ChordsManager(private val context: Context) {
    val regexIsShiftPair = Regex("^(.)SHIFT(.)$")
    private var mode = context.getString(R.string.mode_chars)
    private val numberChords = buildMap<String, String> {
        set("100000", "1SHIFT!")
        set("110000", "2SHIFT@")
        set("010000", "3SHIFT#")
        set("011000", "4SHIFT$")
        set("001000", "5SHIFT%")
        set("000100", "6SHIFT¨")
        set("000110", "7SHIFT&")
        set("000010", "8SHIFT*")
        set("000011", "9SHIFT(")
        set("000001", "0SHIFT)")
        set("100001", "SHIFT")
        set("001100", "DELETE")
        set("111000", " ")
    }
    private val charChords = buildMap<String, String> {
        set("100000", "a")
        set("010000", "e")
        set("001000", "i")
        set("000100", "s")
        set("000010", "r")
        set("000001", "n")
        set("110000", "o")
        set("011000", "u")
        set("100011", "d")
        set("000011", "m")
        set("100010", "c")
        set("010100", "t")
        set("010001", "l")
        set("110001", "p")
        set("000110", "v")
        set("001010", "g")
        set("100100", "h")
        set("011100", "b")
        set("001110", "q")
        set("010010", "x")
        set("001001", "k")
        set("101000", "f")
        set("000101", "j")
        set("101010", "z")
        set("111000", "w")
        set("110010", "y")
        set("000111", " ")
        set("100001", "SHIFT")
        set("001100", "DELETE")
        set("011001", "/SHIFT?")
        set("101001", ";SHIFT:")
        set("001101", "+SHIFT=")
        set("001011", "-SHIFT_")
        set("100111", "~SHIFT^")
        set("001111", "¨")
        set("010111", "´SHIFT`")
        set("100110", "ç")
        set("010110", ",SHIFT<")
        set("010011", ".SHIFT>")
        set("110100", "[SHIFT{")
        set("101100", "]SHIFT}")
    }

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