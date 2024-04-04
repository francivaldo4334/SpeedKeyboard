package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.MotionEvent
import android.view.View
import android.widget.Button

class KeyboardService() : InputMethodService(), View.OnTouchListener {
    private lateinit var buttons: List<Button>
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
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val button = (v as? Button)
        when (event?.action) {
            MotionEvent.ACTION_DOWN ->
                button?.isPressed = true

            MotionEvent.ACTION_UP ->
                button?.isPressed = false

            MotionEvent.ACTION_SCROLL -> {}
        }
        if (buttons.none{it.isPressed}){
            currentInputConnection.apply {
                commitText("TESTE",1)
            }
        }
        return true
    }
}