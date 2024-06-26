package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.getChordId

class KeyActionsController(
    private val context: Context,
    private val othersButtons: MutableMap<Int, MutableMap<Int, Button>> = mutableMapOf()
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
    private val initialVector = mutableMapOf<Int, Pair<Double, Double>>()
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
                var newKey = chordsManager.getKey(chord, isCapslock, isShift)
                newKey = when (newKey) {
                    "DELETE" -> "⌫"
                    "SHIFT" -> "⍙"
                    " " -> "⎵"
                    else -> newKey
                }
                btn.text = newKey
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
                onActionUp(it.value, buttons)
            }
        }
        othersButtons.remove(button.id)
    }

    fun onActionTouch(
        button: Button,
        buttons: List<Button>,
        shortcutButtons: List<Button>,
        event: MotionEvent
    ) {
        val location = IntArray(2)
        button.getLocationOnScreen(location)
        val buttonX = location[0]
        val buttonY = location[1]
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                if (initialVector.containsKey(button.id)) {
                    initialVector.remove(button.id)
                }
                onActionUp(button, buttons)
            }

            MotionEvent.ACTION_DOWN -> {

                vibrate()
                initialVector[button.id] = Pair(
                    event.rawX - buttonX.toDouble(),
                    event.rawY - buttonY.toDouble()
                )
                onActionDown(buttons, button)
            }

            MotionEvent.ACTION_MOVE -> {
                onActionScroll(button, buttons, shortcutButtons, event.rawX, event.rawY)
            }
        }

    }

    @SuppressLint("ServiceCast", "MissingPermission")
    fun vibrate(milliseconds: Long = 100) {
        val amplitude = VibrationEffect.DEFAULT_AMPLITUDE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibrator.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        amplitude
                    )
                )
            )
        } else {
            val vibrator =
                context.getSystemService(@Suppress("DEPRECATION") Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        amplitude
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(milliseconds)
            }
        }
    }

    private fun onActionScroll(
        button: Button,
        buttons: List<Button>,
        shortcutButtons: List<Button>,
        rawX: Float,
        rawY: Float
    ) {
        if (!isWithinBounds(button, rawX, rawY)) {
            val targetButton = findTargetButton(rawX, rawY, buttons)
                ?: findTargetButton(rawX, rawY, shortcutButtons)
            targetButton?.let {
                if (collisionOval(rawX, rawY, it)) {
                    val localButton = if (isSpacerButton(it.id)) {
                        val buttonId = mapSpacerToButtonId(it.id)
                        buttons.find { it.id == buttonId }
                    } else { it }

                    updateOthersButtons(button.id, localButton!!)
                    if (!localButton.isPressed) {
                        simulateTouchEvent(
                            localButton,
                            MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, rawX, rawY, 0)
                        )
                    }
                }
            }
        } else {
            releaseOtherButtons(button.id, buttons)
            othersButtons.remove(button.id)
        }
    }

    private fun collisionOval(rawX: Float, rawY: Float, button: Button): Boolean {
        val (width, height) = button.width to button.height
        val (centerX, centerY) = IntArray(2).apply {
            button.getLocationOnScreen(this)
            this[0] += width / 2
            this[1] += height / 2
        }
        val (pointX, pointY) = rawX + (width / 2) to rawY + (height / 2)
        val normalizedX = (pointX - (centerX + (width / 2))) / (width / 2)
        val normalizedY = (pointY - (centerY + (height / 2))) / (height / 2)
        return (normalizedX * normalizedX) + (normalizedY * normalizedY) <= 1
    }

    private fun releaseOtherButtons(buttonId: Int, buttons: List<Button>) {
        othersButtons[buttonId]?.forEach {
            onActionUp(it.value, buttons)
        }
    }

    private fun updateOthersButtons(buttonId: Int, targetButton: Button) {
        othersButtons[buttonId]?.set(targetButton.id, targetButton) ?: run {
            othersButtons[buttonId] = mutableMapOf(targetButton.id to targetButton)
        }
    }

    private fun mapSpacerToButtonId(id: Int): Int {
        return when (id) {
            R.id.spacer_tl -> R.id.btn2
            R.id.spacer_tr -> R.id.btn0
            R.id.spacer_bl -> R.id.btn5
            R.id.spacer_br -> R.id.btn3
            else -> 0
        }
    }

    private fun isSpacerButton(id: Int): Boolean {
        return id in setOf(R.id.spacer_tl,R.id.spacer_tr,R.id.spacer_bl,R.id.spacer_br)
    }

    private fun isWithinBounds(button: Button, rawX: Float, rawY: Float): Boolean {
        val location = IntArray(2).apply {
            button.getLocationOnScreen(this)
        }
        val (buttonX, buttonY) = location
        val (buttonWidth, buttonHeight) = button.width to button.height

        return rawX.toInt() in buttonX..(buttonX + buttonWidth) && rawY.toInt() in buttonY..(buttonY + buttonWidth)
    }

    private fun simulateTouchEvent(button: Button, motionEvent: MotionEvent) {
        button.dispatchTouchEvent(motionEvent)
    }

    private fun findTargetButton(x: Float, y: Float, buttons: List<Button>): Button? {
        for (button in buttons) {
            val location = IntArray(2).apply {
                button.getLocationOnScreen(this)
            }
            val (buttonX, buttonY) = location
            val (buttonWidth, buttonHeight) = button.width to button.height
            if (x.toInt() in buttonX..(buttonX + buttonWidth) && y.toInt() in buttonY..(buttonY + buttonHeight)) {
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