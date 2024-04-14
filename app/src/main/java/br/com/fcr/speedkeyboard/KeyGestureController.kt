package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.view.View
import android.widget.Button

interface KeyGestureControllerCallback {
    fun onActionUp(button: Button)
    fun onActionDown(button: Button)
    fun onActionScroll(button: Button, x: Float, y: Float)
}

@SuppressLint("ClickableViewAccessibility")
class KeyGestureController(private val callback: KeyGestureControllerCallback) {
    private lateinit var view: View
    fun setView(view: View) {
        this.view = view
    }

    fun onActionUp() {
        callback.onActionUp(view as Button)
    }

    fun onActionDown() {
        callback.onActionDown(view as Button)
    }

    fun onActionMove(x: Float, y: Float) {
        callback.onActionScroll(view as Button, x, y)
    }
}