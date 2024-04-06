package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import br.com.fcr.speedkeyboard.utils.getIdString
import kotlinx.coroutines.DelicateCoroutinesApi

class KeyboardService() : InputMethodService(), View.OnTouchListener {
    private lateinit var buttons: List<Button>
    private val delayTouchTime = 500
    private lateinit var listKeyLastTouchTime: MutableMap<Int, Pair<Boolean,Long>>
    private var resutlIdString = ""
    private lateinit var shiftIndicator: TextView
    private var isCapslock = false
    private var isShift = false
    private val keymaps = buildMap<String,String> {
        set("100000","a")
        set("010000","e")
        set("001000","i")
        set("000100","s")
        set("000010","r")
        set("000001","n")
        set("110000","o")
        set("011000","u")
        set("000110","h")
        set("000011","k")
        set("100100","m")
        set("010010","d")
        set("001001","t")
        set("100010","c")
        set("010001","l")
        set("110100","b")
        set("011010","g")
        set("101001","f")
        set("000101","j")
        set("111000","p")
        set("001101","w")
        set("011100","y")
        set("100011","q")
        set("110001","x")
        set("101010","z")
        set("011001","v")
        set("000111"," ")
        set("100001","SHIFT")
        set("001100","DELETE")
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            shiftIndicator = findViewById(R.id.shift_indicator)
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
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val button = (v as? Button)
        val currencyTime = System.currentTimeMillis()
        buttons.filter { it.isPressed }.forEach {
            listKeyLastTouchTime[it.id]?.let { pair ->
                if (currencyTime - pair.second >= delayTouchTime && pair.first) {
                    listKeyLastTouchTime[it.id] = Pair(false,currencyTime)
                    it.isPressed = false
                    resutlIdString = buttons.getIdString()
                }
            }
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                button?.isPressed = true
                button?.id?.let { id ->
                    listKeyLastTouchTime[id] = Pair(false,System.currentTimeMillis())
                }
                resutlIdString = buttons.getIdString()
            }

            MotionEvent.ACTION_UP -> {
                button?.isPressed = false
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
                if(keymaps.containsKey(resutlIdString)){
                    var command = keymaps[resutlIdString]
                    when(command) {
                        "DELETE" -> {
                            deleteSurroundingText(1,0)
                        }
                        "SHIFT" -> {
                            if (isCapslock){
                                isCapslock = false
                            }
                            else if (isShift){
                                isCapslock = true
                            }
                            else {
                                isShift = true
                            }
                        }
                        else -> {
                            if (isShift || isCapslock){
                                command = command?.uppercase()
                                isShift = false
                            }
                            commitText(command, 1)
                        }
                    }
                }
            }
        }
        Log.d("TS: ", "OK")
        return true
    }
}