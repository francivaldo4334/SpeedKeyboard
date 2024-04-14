package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button


class KeyboardService() : InputMethodService() {
    private lateinit var buttons: List<Button>
    private lateinit var keyActionsController: KeyActionsController
    private lateinit var buttonMode: Button
    private lateinit var buttonConfirm: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            buttonMode = findViewById(R.id.button_mode)
            buttonConfirm = findViewById(R.id.button_confirm)
            buttons = buildList {
                add(findViewById(R.id.btn0))
                add(findViewById(R.id.btn1))
                add(findViewById(R.id.btn2))
                add(findViewById(R.id.btn3))
                add(findViewById(R.id.btn4))
                add(findViewById(R.id.btn5))
            }

            val gestureController = KeyGestureController(object : KeyGestureControllerCallback {
                override fun onActionUp(button: Button) {
                    keyActionsController.onActionUp(button,buttons)
                }

                override fun onActionDown(button: Button) {
                    keyActionsController.onActionDown(buttons, button)
                }

                override fun onActionScroll(button: Button, x: Float, y: Float) {
                    keyActionsController.onActionScroll(button, buttons, x, y)
                }

            })
            buttons.forEach {
                it.setOnTouchListener { v, event ->
                    gestureController.setView(v)
                    when (event.action) {
                        MotionEvent.ACTION_UP ->
                            gestureController.onActionUp()

                        MotionEvent.ACTION_DOWN ->
                            gestureController.onActionDown()

                        MotionEvent.ACTION_MOVE -> {
                            gestureController.onActionMove(event.x, event.y)
                        }
                    }
                    true
                }
            }
            buttonMode.setOnClickListener {
                val textMode = keyActionsController.nextMode()
                keyActionsController.setMode(textMode,buttons)
                (it as Button).text = textMode
            }
            buttonConfirm.setOnClickListener {
                currentInputConnection.apply {
                    sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
                }
            }
            keyActionsController = KeyActionsController(
                buttons
                    .associate { it.id to ButtonStates(false, 0L) }
                    .toMutableMap(),
                currentInputConnection
            )
        }
    }
}