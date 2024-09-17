package com.harissabil.zakatkuy.ui.screen.dashboard_amil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.harissabil.zakatkuy.ui.screen.dashboard_amil.ui.theme.ZakatKuyTheme

class DashboardAmilActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            ZakatKuyTheme {
                val customTabsIntent: CustomTabsIntent = CustomTabsIntent.Builder()
                    .setUrlBarHidingEnabled(false)
                    .setShowTitle(true)
                    .build()
                customTabsIntent.intent.putExtra(
                    Intent.EXTRA_REFERRER,
                    Uri.parse("android-app://" + this.packageName)
                )
                customTabsIntent.launchUrl(
                    this,
                    Uri.parse("https://2vc48s1k-8501.asse.devtunnels.ms/")
                )
            }
        }
    }
}