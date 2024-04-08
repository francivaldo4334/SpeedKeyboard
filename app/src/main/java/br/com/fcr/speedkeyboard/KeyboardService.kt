package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import br.com.fcr.speedkeyboard.utils.getIdString


class KeyboardService() : InputMethodService() {
    private lateinit var buttons: List<Button>
    private lateinit var shiftIndicator: TextView
    private lateinit var keyActionsController: KeyActionsController
    private var isRunnableLongPress = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            shiftIndicator = findViewById(R.id.shift_indicator)
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
                    keyActionsController.disablePressedButtons(buttons)
                    keyActionsController.onActionUp(button)
                    if (keyActionsController.isEndCommand(buttons)) {
                        keyActionsController.loadKeyAction()
                        keyActionsController.execute(currentInputConnection)
                    }
                    isRunnableLongPress = false
                }

                override fun onActionDown(button: Button) {
                    keyActionsController.onActionDown(buttons, button)
                    keyActionsController.loadKeyAction()
                }
                override fun onActionLongPress() {
                }

                override fun onActionDoubleTap() {
                    isRunnableLongPress = true
                    Thread(Runnable {
                        while (isRunnableLongPress) {
                            keyActionsController.execute(currentInputConnection)
                            Thread.sleep(100)
                        }
                        stopSelf()
                    }).start()
                }

            })
            buttons.forEach {
                it.setOnTouchListener { v, event ->
                    gestureController.setView(v)
                    if (event.action == MotionEvent.ACTION_UP) {
                        gestureController.onActionUp()
                    }
                    gestureController
                            .getGestureDetector()
                            .onTouchEvent(event)
                    false
                }
            }
            keyActionsController = KeyActionsController(
                    buttons
                            .associate { it.id to Pair(false, 0L) }
                            .toMutableMap()
            )
        }
    }
}