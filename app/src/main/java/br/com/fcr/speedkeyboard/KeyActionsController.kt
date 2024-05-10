package br.com.fcr.speedkeyboard

import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.getChordId

class KeyActionsController(
    private val context: Context,
    private val othersButtons: MutableMap<Int, MutableMap<Int,Button>> = mutableMapOf()
) {
    private var chordsManager: ChordsManager = ChordsManager(context)
    private var chordId = ""
    private var key = ""
    private var lastKey = ""
    private var lastChord = ""
    private var isDelete = false
    private var isCapslock = false
    private var isShift = false
    private var isRunnableLongPress = false
    private var currentInputConnection: InputConnection? = null
    fun setInputConnection(inputConnection: InputConnection) {
        currentInputConnection = inputConnection
    }

    private fun onActionLongPress(buttons: List<Button>) {
        isRunnableLongPress = true
        Thread {
            while (isRunnableLongPress) {
                loadKeyByChord(buttons.getChordId())
                execute(key)
                Thread.sleep(10)
            }
        }.start()
    }

    private fun execute(key: String) {
        currentInputConnection?.apply {
            when {
                isDelete -> {
                    deleteSurroundingText(1, 0)
                }

                else -> {
                    if (lastKey.isNotEmpty() && key.isNotEmpty() && "^~¨´`".contains(lastKey)) {
                        val newKey = chordsManager.getDiacritic(
                            diacritic = lastKey,
                            key = key
                        )
                        deleteSurroundingText(1, 0)
                        commitText(newKey, 1)
                    } else {
                        commitText(key, 1)
                    }
                }
            }
        }
    }

    private fun loadKeyByChord(chord: String) {
        if (key != "SHIFT" && key != "DELETE" && key != "")
            lastKey = key
        key = ""
        lastChord = chord
        isDelete = false
        if (!chordsManager.containsKey(chord)) {
            return
        }
        when (val newKeyString = chordsManager.getKey(chord, isCapslock, isShift)) {
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

            else -> {
                key = newKeyString
                isShift = false
            }
        }
    }

    private fun onActionDown(buttons: List<Button>, button: Button) {
        button.isPressed = true
        chordId = buttons.getChordId()
        if (chordsManager.getKey(chordId) == "DELETE") {
            Thread {
                val initialTime = System.currentTimeMillis()
                val initialChord = chordId
                var executeLongPress = true
                val timeoutLongPress = 500L
                while (System.currentTimeMillis() - initialTime < timeoutLongPress) {
                    if (initialChord != buttons.getChordId()) {
                        executeLongPress = false
                        break
                    }
                }
                if (executeLongPress) {
                    onActionLongPress(buttons)
                }
            }.start()
        }
        loadPreviousKeys(buttons)
    }

    private fun loadPreviousKeys(buttons: List<Button>) {
        val previousChords = chordsManager.getPreviousAndDisableKeysKeys(chordId)
        buttons.forEach { btn ->
            var isSelected = false
            var chord = ""
            for (it in previousChords) {
                val buttonIdChord = chordsManager.getButtonIdByChord(it.second)
                if (buttonIdChord == btn.id) {
                    chord = it.first
                    isSelected = true
                    break
                }
            }
            if (isSelected) {
                btn.isSelected = true
                btn.text = chordsManager.getKey(chord, isCapslock, isShift)
            } else if (!btn.isPressed) {
                btn.isSelected = false
                btn.text = ""
            }
        }
    }

    private fun onActionUp(button: Button, buttons: List<Button>) {
        isRunnableLongPress = false
        isDelete = false
        button.isPressed = false
        if (buttons.none { it.isPressed }) {
            loadKeyByChord(chordId)
            execute(key)
            val textMode = chordsManager.getMode()
            setMode(textMode, buttons)
        }
        if (othersButtons.containsKey(button.id)) {
            othersButtons[button.id]?.forEach {
                onActionUp(it.value,buttons)
            }
        }
        othersButtons.remove(button.id)
    }

    fun onActionTouch(button: Button, buttons: List<Button>, event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                onActionUp(button, buttons)
            }

            MotionEvent.ACTION_DOWN ->
                onActionDown(buttons, button)

            MotionEvent.ACTION_MOVE -> {
                onActionScroll(button, buttons, event.rawX, event.rawY)
            }
        }

    }

    private fun onActionScroll(button: Button, buttons: List<Button>, rawX: Float, rawY: Float) {
        val location = IntArray(2)
        button.getLocationOnScreen(location)
        val buttonX = location[0]
        val buttonY = location[1]
        val buttonWidth = button.width
        val buttonHeight = button.height
        if (rawX > buttonX + buttonWidth || rawX < buttonX || rawY > buttonY + buttonHeight || rawY < buttonY) {
            val targetButton = findTargetButton(rawX, rawY, buttons)
            targetButton?.let {
                if (othersButtons.containsKey(button.id)){
                    othersButtons[button.id]?.set(it.id, it)
                }
                else {
                    othersButtons[button.id] = mutableMapOf(it.id to it)
                }
                simulateTouchEvent(
                    it,
                    MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, rawX, rawY, 0)
                )
            }
        }
        else {
            if (othersButtons.containsKey(button.id)) {
                othersButtons[button.id]?.forEach {
                    onActionUp(it.value, buttons)
                }
            }
            othersButtons.remove(button.id)
        }
    }

    private fun simulateTouchEvent(button: Button, motionEvent: MotionEvent) {
        button.dispatchTouchEvent(motionEvent)
    }

    private fun findTargetButton(x: Float, y: Float, buttons: List<Button>): Button? {
        for (button in buttons) {
            val location = IntArray(2)
            button.getLocationOnScreen(location)
            val buttonX = location[0]
            val buttonY = location[1]
            val buttonWidth = button.width
            val buttonHeight = button.height
            if (x > buttonX && x < buttonX + buttonWidth && y > buttonY && y < buttonY + buttonHeight) {
                return button
            }
        }
        return null
    }

    fun nextMode(): String {
        val nextMode = when (chordsManager.getMode()) {
            context.getString(R.string.mode_chars) -> context.getString(R.string.mode_number)
            else -> context.getString(R.string.mode_chars)
        }
        return nextMode
    }

    fun setMode(mode: String, buttons: List<Button>) {
        chordsManager.setMode(mode)
        val getKey: (String) -> String = {
            val key = chordsManager.getKey(it, isCapslock, isShift)
            if (chordsManager.regexIsShiftPair.matches(key)) {
                key.split("SHIFT").first()
            } else
                key

        }
        buttons[0].text = getKey("100000")
        buttons[0].isSelected = false
        buttons[1].text = getKey("010000")
        buttons[1].isSelected = false
        buttons[2].text = getKey("001000")
        buttons[2].isSelected = false
        buttons[3].text = getKey("000100")
        buttons[3].isSelected = false
        buttons[4].text = getKey("000010")
        buttons[4].isSelected = false
        buttons[5].text = getKey("000001")
        buttons[5].isSelected = false
    }
}