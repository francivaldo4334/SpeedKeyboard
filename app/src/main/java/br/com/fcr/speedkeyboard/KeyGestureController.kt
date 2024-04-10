package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
interface KeyGestureControllerCallback{
    fun onActionUp(button: Button)
    fun onActionDown(button: Button)
    fun onActionLongPress()
    fun onActionDoubleTap()
}
@SuppressLint("ClickableViewAccessibility")
class KeyGestureController(context: Context,val callback: KeyGestureControllerCallback) {
    private var gestureDetector: GestureDetector
    private lateinit var view: View
    fun setView(view: View) {
        this.view = view
    }
    fun getGestureDetector():GestureDetector{
        return gestureDetector
    }

    fun onActionUp() {
        callback.onActionUp(view as Button)
    }


    init {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                callback.onActionDoubleTap()
                return super.onDoubleTap(e)
            }
            override fun onLongPress(e: MotionEvent) {
                callback.onActionLongPress()
                super.onLongPress(e)
            }

            override fun onDown(e: MotionEvent): Boolean {
                callback.onActionDown(view as Button)
                return super.onDown(e)
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                return super.onScroll(e1, e2, distanceX, distanceY)

            }
        })
    }
}