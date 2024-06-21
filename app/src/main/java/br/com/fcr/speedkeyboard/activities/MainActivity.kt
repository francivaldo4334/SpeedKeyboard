package br.com.fcr.speedkeyboard.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            SpeedKeyboardTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextField(value = value, onValueChange = setValue)
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal))
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone))
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.NumberPassword))
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Ascii))
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email))
                        TextField(value = value, onValueChange = setValue, keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Uri))
                    }
                }
            }
        }
    }
}