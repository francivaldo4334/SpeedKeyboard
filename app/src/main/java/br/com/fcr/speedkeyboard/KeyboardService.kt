package br.com.fcr.speedkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View

class KeyboardService() : InputMethodService(), View.OnTouchListener {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            this.setOnClickListener {
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action){
            MotionEvent.ACTION_DOWN -> {}
            MotionEvent.ACTION_UP -> {}
            MotionEvent.ACTION_SCROLL -> {}
        }
       return true
    }
}