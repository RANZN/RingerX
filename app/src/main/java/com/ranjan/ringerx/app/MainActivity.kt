package com.ranjan.ringerx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ranjan.ringerx.app.ui.ringer_setting.RingerSettingsHost
import com.ranjan.ringerx.app.ui.theme.RingerXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            RingerXTheme {
                RingerSettingsHost()
            }
        }
    }
}