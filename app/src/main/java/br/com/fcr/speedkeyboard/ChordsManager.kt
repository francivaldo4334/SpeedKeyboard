package br.com.fcr.speedkeyboard

class ChordsManager {
    val regexIsShiftPair = Regex("^(.)SHIFT(.)$")
    val regexIsDiacriticChord = Regex("^111[0-1]+$")
    private val numberChords = buildMap<String,String> {
        set("100000", "1")
        set("110000", "2")
        set("010000", "3")
        set("011000", "4")
        set("001000", "5")
        set("000100", "6")
        set("000110", "7")
        set("000010", "8")
        set("000011", "9")
        set("000001", "0")
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
        set("011001","/SHIFT?")
        set("101001",";SHIFT:")
        set("001101","+SHIFT=")
        set("001011","-SHIFT_")
        set("111100","~SHIFT^")
        set("111010","´SHIFT`")
        set("101100","ç")
    }

    fun getKey(chord: String): String {
        return charChords[chord] ?: ""
    }

    fun containsKey(chord: String): Boolean {
        return charChords.containsKey(chord)
    }
}