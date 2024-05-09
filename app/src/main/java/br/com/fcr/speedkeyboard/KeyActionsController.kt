package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.InputConnection
import android.widget.Button
import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import br.com.fcr.speedkeyboard.utils.getChordId

class KeyActionsController(private val context: Context,private val othersButtons: MutableMap<Int, Pair<List<ButtonIdsManager.Directions>, Button>?>) {
    private var chordsManager: ChordsManager = ChordsManager(context)
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
    private var limitDistance: Float? = null
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
                    if (lastKey.isNotEmpty() && key.isNotEmpty() &&  "^~¨´`".contains(lastKey)) {
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
        val instanceOtherButton = othersButtons[button.id]
        othersButtons[button.id] = null
        instanceOtherButton?.let {
            onActionUp(it.second, buttons)
        }
    }

    fun onActionTouch(button: Button, buttons: List<Button>, event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_UP ->
                onActionUp(button, buttons)

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
//        val currentClick: Pair<Double, Double> = Pair(rawX.toDouble(), rawY.toDouble())
//        val initClick: Pair<Double, Double> = Pair((buttonWidth / 2).toDouble(), (buttonHeight / 2).toDouble())
//        val currentDistance = sqrt((currentClick.first - initClick.first).pow(2) + (currentClick.second - initClick.second).pow(2))
//        if (rawX > buttonWidth  || rawX < 0 || rawY > buttonHeight || rawY < 0) {
        if (rawX > buttonX + buttonWidth || rawX < buttonX || rawY > buttonY + buttonHeight || rawY < buttonY) {
            val targetButton = findTargetButton(rawX,rawY, buttons)
            targetButton?.let {
                simulateTouchEvent(it, MotionEvent.ACTION_DOWN, rawX,rawY)
            }
//            val angle = buttonsIdManager.calcAngle(initClick, currentClick)
//            val angleRounded45 = buttonsIdManager.getRound45(angle)
//            if (limitDistance == null && (angleRounded45 % 90).toInt() != 0){
//                val margin = 0.5f
//                val marginW = margin * buttonWidth
//                val marginH = margin * buttonHeight
//                val w = (buttonWidth + marginW)/2
//                val h = (buttonHeight + marginH)/2
//                limitDistance = sqrt(w.pow(2) + h.pow(2))
//            }
//            if (othersButtons[button.id] == null) {
//                val dirs = buttonsIdManager.getDirectionsByRounded45(angleRounded45)
//                val newBtnId = buttonsIdManager.getNextId(button.id, *dirs.toTypedArray())
//                buttons.find { it.id == newBtnId }?.let { newButton ->
//                    othersButtons[button.id] = Pair(dirs, newButton)
//                    onActionDown(buttons, newButton)
//                }
//            }
//            else if (limitDistance == null || currentDistance > limitDistance!!){
//                othersButtons[button.id]?.let { newButton ->
//                    val dirs = newButton.first
//                    val scaleH = when {
//                        ButtonIdsManager.Directions.UP in dirs -> 1
//                        ButtonIdsManager.Directions.DOWN in dirs -> -1
//                        else -> 0
//                    }
//                    val scaleW = when {
//                        ButtonIdsManager.Directions.RIGHT in dirs -> -1
//                        ButtonIdsManager.Directions.LEFT in dirs -> 1
//                        else -> 0
//                    }
//                    val productW = (scaleW * button.width)
//                    val productH = (scaleH * button.height)
//                    val newX = rawX + productW
//                    val newY = rawY + productH
//                    onActionScroll(newButton.second, buttons, newX, newY)
//                }
//            }
        }
        else {
//            othersButtons[button.id]?.let {
//                onActionUp(it.second, buttons)
//            }
//            othersButtons[button.id] = null
        }
    }

    private fun simulateTouchEvent(button: Button, action: Int, x: Float, y: Float) {
        val motionEvent = MotionEvent.obtain(0,0, action, x, y, 0)
        button.onTouchEvent(motionEvent)
        motionEvent.recycle()
    }

    private fun findTargetButton(x: Float, y: Float, buttons: List<Button>): Button? {
        for (button in buttons){
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