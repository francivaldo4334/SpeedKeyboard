package br.com.fcr.speedkeyboard

class ChordsManager {
    private val keymaps = buildMap<String, String> {
        set("100000", "a")
        set("010000", "e")
        set("001000", "i")
        set("000100", "s")
        set("000010", "r")
        set("000001", "n")
        set("110000", "o")
        set("011000", "u")
        set("000110", "h")
        set("000011", "k")
        set("100100", "m")
        set("010010", "d")
        set("001001", "t")
        set("100010", "c")
        set("010001", "l")
        set("110100", "b")
        set("011010", "g")
        set("101000", "f")
        set("000101", "j")
        set("111000", "p")
        set("001101", "w")
        set("011100", "y")
        set("100011", "q")
        set("110001", "x")
        set("101010", "z")
        set("011001", "v")
        set("000111", " ")
        set("010100", "SHIFT")
        set("001010", "DELETE")
    }

    fun getKey(chord: String): String {
        return keymaps[chord] ?: ""
    }

    fun containsKey(chord: String): Boolean {
        return keymaps.containsKey(chord)
    }
}