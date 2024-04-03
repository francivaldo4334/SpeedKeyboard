package br.com.fcr.speedkeyboard

import android.inputmethodservice.InputMethodService
import android.view.View

class KeyboardService (): InputMethodService(), View.OnClickListener{
    private lateinit var keyId:MutableList<Boolean>
    override fun onCreate() {
        super.onCreate()
    }
    fun MutableList<Boolean>.clear(){
    }
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout,null).apply {
            this.setOnClickListener {
                onClick(it)
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
    override fun onClick(v: View?) {
        keyId.clear()
        when(v?.id){
            R.id.btn0 ->{
                keyId[0] = true
            }
            R.id.btn1 ->{
                keyId[1] = true
            }
            R.id.btn2 ->{
                keyId[2] = true
            }
            R.id.btn3 ->{
                keyId[3] = true
            }
        }
    }
}