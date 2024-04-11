package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import kotlin.math.abs

interface KeyGestureControllerCallback {
    fun onActionUp(button: Button)
    fun onActionDown(button: Button)
    fun onActionLongPress()
    fun onActionScroll(button: Button)
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

    fun onActionDown(){
        callback.onActionDown(view as Button)
    }
}