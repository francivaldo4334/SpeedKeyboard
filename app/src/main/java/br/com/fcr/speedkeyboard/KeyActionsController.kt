package br.com.fcr.speedkeyboard

import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import br.com.fcr.speedkeyboard.utils.getChordId
import kotlin.math.pow
import kotlin.math.sqrt

data class ButtonStates(var isActivated: Boolean, var initialPressedTime: Long)
class KeyActionsController(
) {
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
    private var otherButton: Button? = null
    private var currentInputConnection: InputConnection? = null
    fun setInputConnection(inputConnection: InputConnection) {
        currentInputConnection = inputConnection
    }

    private fun onActionLongPress(buttons: List<Button>) {
        isRunnableLongPress = true
        Thread(Runnable {
            while (isRunnableLongPress) {
                loadKeyByChord(buttons.getChordId())
                execute(key)
                Thread.sleep(50)
            }
        }).start()
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

    fun onActionDown(buttons: List<Button>, button: Button) {
        button.isPressed = true
        chordId = buttons.getChordId()
        if (chordsManager.getKey(chordId) == "DELETE") {
            Thread(Runnable {
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
            }).start()
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

    fun onActionUp(button: Button, buttons: List<Button>) {
        isRunnableLongPress = false
        isDelete = false
        button.isPressed = false
        if (buttons.none { it.isPressed }) {
            loadKeyByChord(chordId)
            execute(key)
            val textMode = chordsManager.getMode()
            setMode(textMode, buttons)
        }
        val instanceOtherButton = otherButton
        otherButton = null
        instanceOtherButton?.let {
            onActionUp(it, buttons)
        }
    }

    fun onActionScroll(button: Button, buttons: List<Button>, x: Float, y: Float) {
        val btnW = button.width
        val btnH = button.height
        val currentClick: Pair<Double, Double> = Pair(x.toDouble(), y.toDouble())
        val initClick: Pair<Double, Double> = Pair((btnW / 2).toDouble(), (btnH / 2).toDouble())
        val distance = sqrt(
            (currentClick.first - initClick.first).pow(2) +
                    (currentClick.second - initClick.second).pow(2)
        )
        val limitDistance = sqrt(
            (initClick.first - btnW).pow(2) +
                    (initClick.second - btnH).pow(2)
        )

        if ((x > btnW || x < 0 || y > btnH || y < 0) && distance > limitDistance) {
            val angle = buttonsIdManager.calcAngle(initClick, currentClick)
            val angleRounded45 = buttonsIdManager.getRound45(angle)
            val dirs = buttonsIdManager.getDirectionsByRounded45(angleRounded45)
            val newBtnId = buttonsIdManager.getNextId(button.id, *dirs.toTypedArray())
            if (otherButton == null) {
                otherButton = buttons.find { it.id == newBtnId }
                onActionDown(buttons, otherButton!!)
            }
        } else {
            otherButton?.let {
                onActionUp(it, buttons)
            }
            otherButton = null
        }
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
}