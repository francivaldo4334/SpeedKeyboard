package br.com.fcr.speedkeyboard

class ChordsManager {
    val regexIsShiftPair = Regex("^(.)SHIFT(.)$")
    val regexIsDiacriticChord = Regex("^111[0-1]+$")
    private var mode = "a-z"
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
        set("010001", "t")
        set("010100", "l")
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
        set("110100", "w")
        set("011010", "y")
        set("000111", " ")
        set("100001", "SHIFT")
        set("001100", "DELETE")
        set("011001", "/SHIFT?")
        set("101001", ";SHIFT:")
        set("001101", "+SHIFT=")
        set("001011", "-SHIFT_")
        set("111100", "~SHIFT^")
        set("111010", "´SHIFT`")
        set("101100", "ç")
    }

    fun setMode(mode: String) {
        this.mode = mode
    }

    fun getMode(): String {
        return mode
    }

    fun getKey(chord: String): String {
        return when (mode) {
            "a-z" -> charChords[chord] ?: ""
            "0-9" -> numberChords[chord] ?: ""
            else -> ""
        }
    }

    fun getMapKeys() = when (mode) {
        "a-z" -> charChords
        "0-9" -> numberChords
        else -> emptyMap()
    }

    fun containsKey(chord: String): Boolean {
        return when (mode) {
            "a-z" -> charChords.containsKey(chord)
            "0-9" -> numberChords.containsKey(chord)
            else -> false
        }
    }

    fun getPreviousKeys(chord: String): List<Pair<String, String>> {
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
}