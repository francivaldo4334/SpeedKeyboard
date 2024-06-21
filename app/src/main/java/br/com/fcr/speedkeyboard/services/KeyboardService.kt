package br.com.fcr.speedkeyboard.services

import android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import br.com.fcr.speedkeyboard.KeyActionsController
import br.com.fcr.speedkeyboard.R


class KeyboardService() : InputMethodService() {
    private lateinit var buttons: List<Button>
    private lateinit var shortcutButtons: List<Button>
    private lateinit var keyActionsController: KeyActionsController
    private lateinit var buttonMode: Button
    private lateinit var buttonConfirm: Button
    private var inputTypeClass = EditorInfo.TYPE_CLASS_TEXT
    private var actionTypeClass = EditorInfo.IME_ACTION_NONE

    @SuppressLint("ClickableViewAccessibility", "InflateParams")
    override fun onCreateInputView(): View {
        return layoutInflater.inflate(R.layout.keyboard_layout, null).apply {
            buttonMode = findViewById(R.id.button_mode)
            buttonConfirm = findViewById(R.id.button_confirm)
            buttons = listOf(
                findViewById(R.id.btn0),
                findViewById(R.id.btn1),
                findViewById(R.id.btn2),
                findViewById(R.id.btn3),
                findViewById(R.id.btn4),
                findViewById(R.id.btn5),
            )
            shortcutButtons = listOf(
                findViewById(R.id.spacer_tl),
                findViewById(R.id.spacer_tr),
                findViewById(R.id.spacer_bl),
                findViewById(R.id.spacer_br),
            )

            buttons.forEach { button ->
                button.setOnTouchListener { view, event ->
                    keyActionsController.setInputConnection(currentInputConnection)
                    keyActionsController.onActionTouch(
                        view as Button,
                        buttons,
                        shortcutButtons,
                        event
                    )
                    true
                }
            }
            buttonMode.setOnClickListener { view ->
                keyActionsController.vibrate()
                val textMode = keyActionsController.nextMode()
                keyActionsController.setMode(textMode, buttons)
                (view as Button).text = textMode
            }
            buttonConfirm.setOnClickListener {
                keyActionsController.vibrate()
                val editorInfo = currentInputEditorInfo
                editorInfo?.let {
                    val imeOptionsActionId = editorInfo.imeOptions and EditorInfo.IME_MASK_ACTION
                    currentInputConnection.apply {
                        when (imeOptionsActionId) {
                            EditorInfo.IME_ACTION_NONE -> {
                                sendKeyEvent(
                                    KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER)
                                )
                                sendKeyEvent(
                                    KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER)
                                )
                            }

                            else ->
                                performEditorAction(imeOptionsActionId)
                        }
                    }
                }
            }
            keyActionsController = KeyActionsController(this@KeyboardService)
        }
    }

    override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
        inputTypeClass = editorInfo!!.inputType and EditorInfo.TYPE_MASK_CLASS
        actionTypeClass = editorInfo!!.imeOptions and (EditorInfo.IME_MASK_ACTION or EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        when (inputTypeClass) {
            EditorInfo.TYPE_CLASS_PHONE, EditorInfo.TYPE_CLASS_NUMBER -> {
                val textMode = getString(R.string.mode_number)
                keyActionsController.setMode(textMode, buttons)
                buttonMode.text = textMode
            }
            else -> {
                val textMode = getString(R.string.mode_chars)
                keyActionsController.setMode(textMode, buttons)
                buttonMode.text = textMode
            }
        }
        val textActionIme = when(actionTypeClass) {
            EditorInfo.IME_ACTION_NONE -> {}
            EditorInfo.IME_ACTION_GO -> {}
            EditorInfo.IME_ACTION_DONE -> {}
            EditorInfo.IME_ACTION_NEXT -> {}
            EditorInfo.IME_ACTION_SEND -> {}
            EditorInfo.IME_ACTION_SEARCH -> {}
            EditorInfo.IME_ACTION_PREVIOUS -> {}
            EditorInfo.IME_ACTION_UNSPECIFIED -> {}
            else -> {}
        }
        super.onStartInputView(editorInfo, restarting)
    }
}