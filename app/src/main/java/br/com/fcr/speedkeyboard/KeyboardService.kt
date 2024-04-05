package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.getIdString

class KeyboardService() : InputMethodService(), View.OnTouchListener {
    private lateinit var buttons: List<Button>
    private var lastTouchTime = 0L
    private val delayTouchTime = 500
    private lateinit var mapKeyLastTouchTime: List<MutableMap<Int, Long>>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            buttons = buildList {
                add(findViewById(R.id.btn0))
                add(findViewById(R.id.btn1))
                add(findViewById(R.id.btn2))
                add(findViewById(R.id.btn3))
                add(findViewById(R.id.btn4))
                add(findViewById(R.id.btn5))
            }
            buttons[0].setOnTouchListener(this@KeyboardService)
            buttons[1].setOnTouchListener(this@KeyboardService)
            buttons[2].setOnTouchListener(this@KeyboardService)
            buttons[3].setOnTouchListener(this@KeyboardService)
            buttons[4].setOnTouchListener(this@KeyboardService)
            buttons[5].setOnTouchListener(this@KeyboardService)
            mapKeyLastTouchTime = buttons.map {
                mutableMapOf(it.id to 0L)
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val button = (v as? Button)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                button?.isPressed = true
                button?.id?.let { id ->
                    mapKeyLastTouchTime.find { it.containsKey(id) }
                        ?.set(id, System.currentTimeMillis())
                }
            }

            MotionEvent.ACTION_UP -> {
                val currencyTime = System.currentTimeMillis()
                if (currencyTime - lastTouchTime >= delayTouchTime)
                    button?.isPressed = false
            }

            MotionEvent.ACTION_SCROLL -> {}
        }
        if (buttons.none { it.isPressed }) {
            currentInputConnection.apply {
                commitText(buttons.getIdString(), 1)
            }
        }
        Log.d("IT:", mapKeyLastTouchTime.joinToString { "${it.keys}: ${it.values}\n" })
        return true
    }
}