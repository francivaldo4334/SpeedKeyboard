package br.com.fcr.speedkeyboard.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import br.com.fcr.speedkeyboard.activities.ui.theme.SpeedKeyboardTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val (value, setValue) = remember {
                mutableStateOf("")
            }
            var expand by remember {
                mutableStateOf(false)
            }
            var imeAction by remember {
                mutableStateOf(ImeAction.Default)
            }
            SpeedKeyboardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = imeAction
                        )
                        )
                        Button(onClick = { expand = true}) {
                            Text(text = imeAction.toString())
                        }
                        DropdownMenu(expanded = expand, onDismissRequest = {expand = false}) {
                            for (action in setOf(
                                ImeAction.Default,
                                ImeAction.None,
                                ImeAction.Go,
                                ImeAction.Done,
                                ImeAction.Next,
                                ImeAction.Previous,
                                ImeAction.Search,
                                ImeAction.Send,
                            )){
                                DropdownMenuItem(text = {
                                    Text(text = action.toString())
                                }, onClick = {
                                    imeAction = action
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}