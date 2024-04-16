package br.com.fcr.speedkeyboard

import android.view.MotionEvent
import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import br.com.fcr.speedkeyboard.utils.getChordId

class KeyActionsController(private val othersButtons: MutableMap<Int, Button?>) {
    private var chordsManager: ChordsManager = ChordsManager()
    private var chordId = ""
    private var key = ""
    private var lastKey = ""
    private var lastChord = ""
    private var isDelete = false
    private var isCapslock = false
    private var isShift = false
    private var buttonsIdManager: ButtonIdsManager = ButtonIdsManager()
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
                    if (lastKey.isNotBlank() && key.isNotBlank() && lastChord.isNotBlank() && chordsManager.regexIsDiacriticChord.matches(
                            lastChord
                        )
                    ) {
                        commitText(key, 1)
                    } else {
                        commitText(key, 1)
                    }
                }
            }
        }
    }

    private fun loadKeyByChord(chord: String) {
        key = ""
        lastKey = key
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
                isShift = false
                key = newKeyString
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
        val instanceOtherButton = othersButtons[button.id]
        othersButtons[button.id] = null
        instanceOtherButton?.let {
            onActionUp(it, buttons)
        }
    }

    private fun onActionScroll(button: Button, buttons: List<Button>, x: Float, y: Float) {
        val btnW = button.width
        val btnH = button.height
        val currentClick: Pair<Double, Double> = Pair(x.toDouble(), y.toDouble())
        val initClick: Pair<Double, Double> = Pair((btnW / 2).toDouble(), (btnH / 2).toDouble())

        if ((x > btnW || x < 0 || y > btnH || y < 0)) {
            val angle = buttonsIdManager.calcAngle(initClick, currentClick)
            val angleRounded45 = buttonsIdManager.getRound45(angle)
            val dirs = buttonsIdManager.getDirectionsByRounded45(angleRounded45)
            val newBtnId = buttonsIdManager.getNextId(button.id, *dirs.toTypedArray())
            if (othersButtons[button.id] == null) {
                othersButtons[button.id] = buttons.find { it.id == newBtnId }
                val newButton = othersButtons[button.id]!!
                onActionDown(buttons, newButton)
            }
        } else {
            othersButtons[button.id]?.let {
                onActionUp(it, buttons)
            }
            othersButtons[button.id] = null
        }
//        othersButtons[button.id]?.let { newButton ->
//            val scaleW =
//                when (newButton.id) {
//                    in listOf(R.id.btn0, R.id.btn3) -> 0
//                    in listOf(R.id.btn1,R.id.btn4) -> 1
//                    else -> 2
//                }
//            val scaleH =
//                when(newButton.id) {
//                    in listOf(R.id.btn0,R.id.btn1, R.id.btn2) -> 0
//                    else -> 1
//                }
//
//            onActionScroll(newButton, buttons, x - (button.width * scaleW), y - (button.height * scaleH))
//        }
    }

    fun nextMode(): String {
        val nextMode = when (chordsManager.getMode()) {
            "a-z" -> "0-9"
            else -> "a-z"
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

    fun onActionTouch(button: Button, buttons: List<Button>, event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_UP ->
                onActionUp(button, buttons)

            MotionEvent.ACTION_DOWN ->
                onActionDown(buttons, button)

            MotionEvent.ACTION_MOVE -> {
                onActionScroll(button, buttons, event.x, event.y)
            }
        }

    }
}