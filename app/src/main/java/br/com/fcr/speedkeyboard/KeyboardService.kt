package br.com.fcr.speedkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View

class KeyboardService() : InputMethodService(), View.OnKeyListener {
    private lateinit var keyId: MutableList<Boolean>
    private var initClick = false
    private var endClick = false
    override fun onCreate() {
        super.onCreate()
    }

    fun MutableList<Boolean>.clear() {
    }

    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            this.setOnClickListener {
                //caracteres antes
//                currentInputConnection.getTextBeforeCursor()
                //caracteres apos
//                currentInputConnection.getTextAfterCursor()
                //deletar caracteres especificos
//                currentInputConnection.deleteSurroundingText()
                //adiciona um texto
//                currentInputConnection.commitText()
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        keyId.clear()
        when (v?.id) {
            R.id.btn0 -> {
                keyId[0] = true
            }

            R.id.btn1 -> {
                keyId[1] = true
            }

            R.id.btn2 -> {
                keyId[2] = true
            }

            R.id.btn3 -> {
                keyId[3] = true
            }
        }
        return true
    }
}