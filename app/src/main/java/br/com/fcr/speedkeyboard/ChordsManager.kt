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
        set("011001","?")
    }

    fun getKey(chord: String): String {
        return keymaps[chord] ?: ""
    }

    fun containsKey(chord: String): Boolean {
        return keymaps.containsKey(chord)
    }
}