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
    private lateinit var btn0:Button
    private lateinit var btn1:Button
    private lateinit var btn2:Button
    private lateinit var btn3:Button
    private lateinit var btn4:Button
    private lateinit var btn5:Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            btn0 = findViewById(R.id.btn0)
            btn1 = findViewById(R.id.btn1)
            btn2 = findViewById(R.id.btn2)
            btn3 = findViewById(R.id.btn3)
            btn4 = findViewById(R.id.btn4)
            btn5 = findViewById(R.id.btn5)
            btn0.setOnTouchListener(this@KeyboardService)
            btn1.setOnTouchListener(this@KeyboardService)
            btn2.setOnTouchListener(this@KeyboardService)
            btn3.setOnTouchListener(this@KeyboardService)
            btn4.setOnTouchListener(this@KeyboardService)
            btn5.setOnTouchListener(this@KeyboardService)
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_DOWN -> {
                v?.setBackgroundResource(R.color.purple_500)
                currentInputConnection.apply {
//                    setComposingText("Composi",1)
//                    setComposingText("Composin",1)
//                    commitText("Composing",1)
                }
            }
            MotionEvent.ACTION_UP -> {
                v?.setBackgroundResource(R.color.white)
            }
            MotionEvent.ACTION_SCROLL -> {
                Toast.makeText(this,"SCROLLING",Toast.LENGTH_LONG).show()
            }
        }
       return true
    }
}