package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button


class KeyboardService() : InputMethodService() {
    private lateinit var buttons: List<Button>
    private lateinit var keyActionsController: KeyActionsController
    private lateinit var buttonMode: Button
    private lateinit var buttonConfirm: Button

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
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

            buttons.forEach {
                it.setOnTouchListener { v, event ->
                    val button = v as Button
                    keyActionsController.setInputConnection(currentInputConnection)
                    when (event.action) {
                        MotionEvent.ACTION_UP ->
                            keyActionsController.onActionUp(button, buttons)

                        MotionEvent.ACTION_DOWN ->
                            keyActionsController.onActionDown(buttons, button)

                        MotionEvent.ACTION_MOVE -> {
                            keyActionsController.onActionScroll(button, buttons, event.x, event.y)
                        }
                    }
                    true
                }
            }
            buttonMode.setOnClickListener {
                val textMode = keyActionsController.nextMode()
                keyActionsController.setMode(textMode, buttons)
                (it as Button).text = textMode
            }
            buttonConfirm.setOnClickListener {
                val editorInfo = currentInputEditorInfo
                if (editorInfo != null) {
                    val imeOptionsActionId = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
                    currentInputConnection.apply {
                        sendKeyEvent(
                            KeyEvent(
                                KeyEvent.ACTION_DOWN,
                                imeOptionsActionId
                            )
                        )
                    }
                }
            }
            keyActionsController = KeyActionsController()
        }
    }
}