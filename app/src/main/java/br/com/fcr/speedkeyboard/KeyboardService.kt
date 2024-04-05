package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.versionedparcelable.ParcelImpl
import br.com.fcr.speedkeyboard.utils.getIdString
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KeyboardService() : InputMethodService(), View.OnTouchListener {
    private lateinit var buttons: List<Button>
    private val delayTouchTime = 500
    private lateinit var listKeyLastTouchTime: MutableMap<Int, Pair<Boolean,Long>>

    @OptIn(DelicateCoroutinesApi::class)
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
            listKeyLastTouchTime = mutableMapOf()
            buttons.forEach {
                listKeyLastTouchTime[it.id] = Pair(false,0L)
            }
            GlobalScope.launch {
                while (true){
                    Log.d("TESTE:", "TESTE")
                    val currencyTime = System.currentTimeMillis()
                    buttons.filter { it.isPressed }.forEach {
                        listKeyLastTouchTime[it.id]?.let { pair ->
                            if (currencyTime - pair.second >= delayTouchTime && pair.first) {
                                listKeyLastTouchTime[it.id] = Pair(false,currencyTime)
                                it.isPressed = false
                            }
                        }
                    }
                    delay(300)
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val button = (v as? Button)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                button?.isPressed = true
                button?.id?.let { id ->
                    listKeyLastTouchTime[id] = Pair(false,System.currentTimeMillis())
                }
                Log.d("BTN:","click")
            }

            MotionEvent.ACTION_UP -> {
                button?.id?.let {id ->
                    listKeyLastTouchTime[id]?.let {
                        listKeyLastTouchTime[id] = Pair(true,it.second)
                    }
                }
            }

            MotionEvent.ACTION_SCROLL -> {}
        }
        if (buttons.none { it.isPressed }) {
            currentInputConnection.apply {
                commitText(buttons.getIdString(), 1)
            }
        }
        return true
    }
}