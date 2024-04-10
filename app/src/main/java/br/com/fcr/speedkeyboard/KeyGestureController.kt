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
    fun onActionUp(button: Button, vararg directions: ButtonIdsManager.Directions)
    fun onActionDown(button: Button)
    fun onActionLongPress()
    fun onActionDoubleTap()
    fun onActionScroll(button: Button, vararg directions: ButtonIdsManager.Directions)
}

@SuppressLint("ClickableViewAccessibility")
class KeyGestureController(context: Context, val callback: KeyGestureControllerCallback) {
    private val gestureDetector: GestureDetector
    private val buttonIdsManager: ButtonIdsManager = ButtonIdsManager()
    private lateinit var view: View
    fun setView(view: View) {
        this.view = view
    }

    fun getGestureDetector(): GestureDetector {
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
                val deltaY = e2.y - (e1?.y ?: 0f)
                val scrollThreshold = 100
                val isScrollComplete = abs(deltaY) > scrollThreshold

                val button = (view as Button)
                var directions: List<ButtonIdsManager.Directions> = emptyList()
                e1?.let {
                    val angle = buttonIdsManager.calcAngle(
                            Pair(e1.x.toDouble(), e1.y.toDouble()),
                            Pair(e2.x.toDouble(), e2.y.toDouble())
                    )
                    val rounded45 = buttonIdsManager.getRound45(angle)
                    directions = buttonIdsManager.getDirectionsByRounded45(rounded45)
                }
                callback.onActionScroll(button = button, directions = directions.toTypedArray())
                if (isScrollComplete) {
                    callback.onActionUp(button = button, directions = directions.toTypedArray())
                }
                return super.onScroll(e1, e2, distanceX, distanceY)

            }
        })
    }
}