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
        keyString = chordsManager.getKey(resultIdString)
        when (keyString) {
            "DELETE" -> {
                isDelete = true
                keyString = ""
            }

            "SHIFT" -> {
                if (isCapslock) {
                    isCapslock = false
                } else if (isShift) {
                    isCapslock = true
                } else {
                    isShift = true
                }
                keyString = ""
            }

            else -> if (isShift || isCapslock) {
                keyString = keyString.uppercase()
                isShift = false
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