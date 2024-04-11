package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import br.com.fcr.speedkeyboard.utils.ButtonIdsManager
import br.com.fcr.speedkeyboard.utils.getChordId


class KeyboardService() : InputMethodService() {
    private lateinit var buttons: List<Button>
    private lateinit var keyActionsController: KeyActionsController
    private var isRunnableLongPress = false

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            buttons = buildList {
                add(findViewById(R.id.btn0))
                add(findViewById(R.id.btn1))
                add(findViewById(R.id.btn2))
                add(findViewById(R.id.btn3))
                add(findViewById(R.id.btn4))
                add(findViewById(R.id.btn5))
            }
            val gestureController = KeyGestureController(this@KeyboardService, object : KeyGestureControllerCallback {
                override fun onActionUp(button: Button) {
//                    val buttonId = ButtonIdsManager().getNextId(button.id,*directions)
//                    val button_ = buttons.find { it.id == buttonId }!!
                    isRunnableLongPress = false
                    keyActionsController.onActionUp(button,buttons)
                    if (keyActionsController.isEndCommand(buttons)) {
                        keyActionsController.loadKeyByChord(keyActionsController.chordId)
                        keyActionsController.execute(
                                keyActionsController.key,
                                currentInputConnection
                        )
                    }

                }

                override fun onActionDown(button: Button) {
                    keyActionsController.onActionDown(buttons, button)
                    Thread(Runnable {
                        val initialTime = System.currentTimeMillis()
                        val initialChord = keyActionsController.chordId
                        var executeLongPress = true
                        val timeoutLongPress = 500L
                        while (System.currentTimeMillis() - initialTime < timeoutLongPress){
                            if (initialChord != buttons.getChordId()){
                                executeLongPress = false
                                break
                            }
                        }
                        if (executeLongPress){
                            keyActionsController.loadKeyByChord(initialChord)
                            onActionLongPress()
                        }
                    }).start()
                }
                override fun onActionLongPress() {
                    isRunnableLongPress = true
                    Thread(Runnable {
                        while (isRunnableLongPress) {
                            keyActionsController.execute(keyActionsController.key,currentInputConnection)
                            Thread.sleep(50)
                        }
                        stopSelf()
                    }).start()
                }

                override fun onActionScroll(button: Button) {
//                    val newButtonId = ButtonIdsManager().getNextId(button.id, *directions)
//                    val newButton = buttons.find { it.id == newButtonId }
//                    newButton?.let { onActionDown(it) }

                }

            })
            buttons.forEach {
                it.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    println("ok")
                }
                it.setOnTouchListener { v, event ->
                    gestureController.setView(v)
                    when (event.action){
                        MotionEvent.ACTION_UP -> {
                            gestureController.onActionUp()
                        }
                        MotionEvent.ACTION_DOWN -> {
                            gestureController.onActionDown()
                        }
                    }
//                    if (event.action == MotionEvent.ACTION_UP) {
//                        gestureController.onActionUp()
//                    }
//                    gestureController
//                            .getGestureDetector()
//                            .onTouchEvent(event)
                    true
                }
            }
            keyActionsController = KeyActionsController(
                    buttons
                            .associate { it.id to ButtonStates(false, 0L) }
                            .toMutableMap()
            )
        }
    }
}