package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlin.math.log

class KeyboardService() : InputMethodService(), View.OnTouchListener {
    private lateinit var btn: List<Button>
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            btn = buildList {
                add(findViewById(R.id.btn0))
                add(findViewById(R.id.btn1))
                add(findViewById(R.id.btn2))
                add(findViewById(R.id.btn3))
                add(findViewById(R.id.btn4))
                add(findViewById(R.id.btn5))
            }
            btn[0].setOnTouchListener(this@KeyboardService)
            btn[1].setOnTouchListener(this@KeyboardService)
            btn[2].setOnTouchListener(this@KeyboardService)
            btn[3].setOnTouchListener(this@KeyboardService)
            btn[4].setOnTouchListener(this@KeyboardService)
            btn[5].setOnTouchListener(this@KeyboardService)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                (v as? Button)?.isPressed = true
            }

            MotionEvent.ACTION_UP -> {
                (v as? Button)?.isPressed = false
            }

            MotionEvent.ACTION_SCROLL -> {
            }
        }
        if (btn.none{it.isPressed}){
            currentInputConnection.apply {
                commitText("TESTE",1)
            }
        }
        return true
    }
}