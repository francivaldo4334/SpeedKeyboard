package br.com.fcr.speedkeyboard.utils

import android.widget.Button

fun List<Button>.getIdString():String{
    var idString = ""
    forEach {
        idString+= if (it.isPressed) 1 else 0
    }
    return idString
}