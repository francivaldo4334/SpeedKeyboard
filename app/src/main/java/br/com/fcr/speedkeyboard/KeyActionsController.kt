package br.com.fcr.speedkeyboard

import android.util.Log
import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import br.com.fcr.speedkeyboard.utils.getChordId
import kotlin.math.pow
import kotlin.math.sqrt

data class ButtonStates(var isActivated: Boolean, var initialPressedTime: Long)
class KeyActionsController(
    private val buttonStates: MutableMap<Int, ButtonStates>,
    private val currentInputConnection: InputConnection
) {
    private var chordsManager: ChordsManager = ChordsManager()
    var chordId = ""
    var key = ""
    private var lastKey = ""
    private var lastChord = ""
    private var isDelete = false
    private var isCapslock = false
    private var isShift = false
    private var buttonsIdManager: ButtonIdsManager = ButtonIdsManager()
    private var isRunnableLongPress = false
    var otherButton: Button? = null
    fun onActionDown(buttons: List<Button>, button: Button) {
        button.isPressed = true
        buttonStates[button.id] = ButtonStates(false, System.currentTimeMillis())
        chordId = buttons.getChordId()
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

    fun onActionLongPress(buttons: List<Button>) {
        isRunnableLongPress = true
        Thread(Runnable {
            while (isRunnableLongPress) {
                loadKeyByChord(buttons.getChordId())
                execute(key)
                Thread.sleep(50)
            }
        }).start()
    }

    fun onActionUp(button: Button, buttons: List<Button>) {
        isRunnableLongPress = false
        isDelete = false
        button.isPressed = false
        val state = buttonStates[button.id]!!
        buttonStates[button.id] = ButtonStates(true, state.initialPressedTime)
        if (isEndCommand(buttons)) {
            loadKeyByChord(chordId)
            execute(key)
        }
        val instanceOtherButton = otherButton
        otherButton = null
        instanceOtherButton?.let {
            onActionUp(it, buttons)
        }
    }

    fun isEndCommand(buttons: List<Button>): Boolean {
        return buttons.none { it.isPressed }
    }

    fun loadKeyByChord(chord: String) {
        lastKey = key
        lastChord = chord
        if (!chordsManager.containsKey(chord)) {
            key = ""
            return
        }
        isDelete = false
        key = ""
        var newKeyString = chordsManager.getKey(chord)
        var listCharacters: List<String> = listOf()
        val previousShift = chordsManager.regexIsShiftPair.matches(newKeyString)
        if (previousShift) {
            listCharacters = newKeyString.split("SHIFT")
            newKeyString = listCharacters.first()
        }
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
                key = if (previousShift) listCharacters.last() else newKeyString.uppercase()
                isShift = false
            } else {
                key = newKeyString
            }
        }
    }

    fun execute(key: String) {
        currentInputConnection.apply {
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

    fun setMode(mode: String, buttons: List<Button>) {
        chordsManager.setMode(mode)
        val getKey: (String) -> String = {
            val key = chordsManager.getKey(it)
            if (chordsManager.regexIsShiftPair.matches(key)) {
                key.split("SHIFT").first()
            } else
                key

        }
        buttons[0].text = getKey("100000")
        buttons[1].text = getKey("010000")
        buttons[2].text = getKey("001000")
        buttons[3].text = getKey("000100")
        buttons[4].text = getKey("000010")
        buttons[5].text = getKey("000001")
    }

    fun nextMode(): String {
        val nextMode = when (chordsManager.getMode()) {
            "a-z" -> "0-9"
            else -> "a-z"
        }
        return nextMode
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

        if (
            (
                    x > btnW ||
                            x < 0 ||
                            y > btnH ||
                            y < 0
                    ) &&
            distance > limitDistance
        ) {
            val angle = buttonsIdManager.calcAngle(
                initClick,
                currentClick
            )
            val angleRounded45 = buttonsIdManager.getRound45(angle)
            val dirs = buttonsIdManager.getDirectionsByRounded45(angleRounded45)
            val newBtnId = buttonsIdManager.getNextId(button.id, *dirs.toTypedArray())
            if (otherButton == null) {
                otherButton = buttons.find { it.id == newBtnId }
                onActionDown(buttons, otherButton!!)
            }
        }
    }
}