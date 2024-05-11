package br.com.fcr.speedkeyboard

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.view.inputmethod.EditorInfo.IME_ACTION_NONE


class KeyboardService() : InputMethodService() {
    private lateinit var buttons: List<Button>
    private lateinit var shortcutButtons: List<Button>
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
            shortcutButtons = buildList {
                add(findViewById(R.id.spacer_tl))
                add(findViewById(R.id.spacer_tr))
                add(findViewById(R.id.spacer_bl))
                add(findViewById(R.id.spacer_br))
            }

            buttons.forEach {
                it.setOnTouchListener { v, event ->
                    keyActionsController.setInputConnection(currentInputConnection)
                    keyActionsController.onActionTouch(v as Button,buttons, shortcutButtons,event)
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
                        when(imeOptionsActionId){
                            IME_ACTION_NONE -> {
                                sendKeyEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_DOWN,
                                        KeyEvent.KEYCODE_ENTER
                                    )
                                )
                                sendKeyEvent(
                                    KeyEvent(
                                        KeyEvent.ACTION_UP,
                                        KeyEvent.KEYCODE_ENTER
                                    )
                                )
                            }
                            else -> {
                                performEditorAction(imeOptionsActionId)
                            }
                        }
                    }
                }
            }
            keyActionsController = KeyActionsController(
                context=this@KeyboardService,
            )
        }
    }
}