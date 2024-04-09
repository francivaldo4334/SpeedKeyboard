package br.com.fcr.speedkeyboard

import android.util.Log
import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.getChordId

data class ButtonStates(var isActivated: Boolean, var initialPressedTime: Long)
class KeyActionsController(val buttonStates: MutableMap<Int, ButtonStates>) {
    var chordsManager: ChordsManager = ChordsManager()
    val delayTouchTime = 500
    var chordId = ""
    var keyString = ""
    var isDelete = false
    var isCapslock = false
    var isShift = false
    fun checkTimeoutForDisableButtons(buttons: List<Button>) {
        val currencyTime = System.currentTimeMillis()
        buttons.filter { it.isPressed }.forEach {
            val state = buttonStates[it.id]!!
            val deltaTime = currencyTime - state.initialPressedTime
            if (deltaTime >= delayTouchTime && state.isActivated) {
                it.isPressed = false
                buttonStates[it.id] = ButtonStates(false, currencyTime)
                chordId = buttons.getChordId()
            }
        }
    }

    fun onActionDown(buttons: List<Button>, button: Button) {
        button.isPressed = true
        buttonStates[button.id] = ButtonStates(false, System.currentTimeMillis())
        chordId = buttons.getChordId()
        loadKeyByChord()
    }

    fun onActionUp(button: Button, buttons: List<Button>) {
        checkTimeoutForDisableButtons(buttons)
        isDelete = false
        button.isPressed = false
        val state = buttonStates[button.id]!!
        buttonStates[button.id] = ButtonStates(true, state.initialPressedTime)
    }

    fun isEndCommand(buttons: List<Button>): Boolean {
        return buttons.none { it.isPressed }
    }

    fun loadKeyByChord() {
        if (!chordsManager.containsKey(chordId)) {
            return
        }
        keyString = ""
        val newKeyString = chordsManager.getKey(chordId)
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
        loadKeyByChord()
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