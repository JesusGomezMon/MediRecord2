package com.example.medirecord4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.SideEffect
import androidx.navigation.compose.rememberNavController
import com.example.medirecord4.ui.nav.AppNavHost
import com.example.medirecord4.ui.theme.Background
import com.example.medirecord4.ui.theme.MediRecordTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediRecordTheme {
                Surface(color = Background) {
                    val nav = rememberNavController()
                    AppNavHost(navController = nav)
                }
            }
        }
    }
}
