package br.com.fcr.speedkeyboard

import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.getIdString

class KeyActionsController(val listKeyLastTouchTime: MutableMap<Int, Pair<Boolean, Long>>) {
    var chordsManager: ChordsManager = ChordsManager()
    val delayTouchTime = 500
    var resultIdString = ""
    var keyString = ""
    var isDelete = false
    var isCapslock = false
    var isShift = false
    var keyIdAndTimeState = Pair("",0L)
    fun disablePressedButtons(buttons: List<Button>) {
        val currencyTime = System.currentTimeMillis()
        buttons.filter { it.isPressed }.forEach {
            listKeyLastTouchTime[it.id]?.let { pair ->
                if (currencyTime - pair.second >= delayTouchTime && pair.first) {
                    listKeyLastTouchTime[it.id] = Pair(false, currencyTime)
                    it.isPressed = false
                    resultIdString = buttons.getIdString()
                }
            }
        }
    }

    fun onActionDown(buttons: List<Button>, button: Button) {
        button.isPressed = true
        button.id.let { id ->
            listKeyLastTouchTime[id] = Pair(false, System.currentTimeMillis())
        }
        resultIdString = buttons.getIdString()
        keyIdAndTimeState = Pair(resultIdString, System.currentTimeMillis())
    }

    fun onActionUp(button: Button) {
        if (isDelete) {
            isDelete = false
        }
        button.isPressed = false
        button.id.let { id ->
            listKeyLastTouchTime[id]?.let {
                listKeyLastTouchTime[id] = Pair(true, it.second)
            }
        }
        keyString = ""
    }

    fun isEndCommand(buttons: List<Button>): Boolean {
        return buttons.none { it.isPressed }
    }

    fun loadKeyAction() {
        if (!chordsManager.containsKey(resultIdString)) {
            return
        }
        keyString = ""
        val newKeyString = chordsManager.getKey(resultIdString)
        when (newKeyString) {
            "DELETE" -> {
                isDelete = true
            }

            "SHIFT" -> {
                if (isCapslock) {
                    isCapslock = false
                } else if (isShift) {
                    isCapslock = true
                } else {
                    isShift = true
                }
            }

            else -> if (isShift || isCapslock) {
                keyString = newKeyString.uppercase()
                isShift = false
            } else {
                keyString = newKeyString
            }
        }
    }

    fun execute(currentInputConnection: InputConnection) {
        currentInputConnection.apply {
            when {
                isDelete -> {
                    deleteSurroundingText(1, 0)
                }

                else -> {
                    commitText(keyString, 1)
                }
            }
        }
    }
}