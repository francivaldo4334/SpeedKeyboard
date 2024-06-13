package br.com.fcr.speedkeyboard.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("AppCompatCustomView")
class RoundedButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Button(context, attrs, defStyleAttr) {
    private val radius = 16.dpToPx()
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x  = event?.x?:0f
        val y = event?.y?:0f

        val width = width
        val height = height

        val centerX = width / 2
        val centerY = height / 2

        // Verifique se o toque está dentro do círculo com raio definido
        val distance = sqrt((x - centerX).toDouble().pow(2.0) + (y - centerY).toDouble().pow(2.0))
        return if (distance <= radius) {
            super.onTouchEvent(event)
        } else {
            false
        }
    }
    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }

}