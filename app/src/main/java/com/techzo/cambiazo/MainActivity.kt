package com.techzo.cambiazo

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.techzo.cambiazo.presentation.navigate.NavScreen
import com.techzo.cambiazo.presentation.navigate.NavigationViewModel
import com.techzo.cambiazo.presentation.profile.subscription.SubscriptionViewModel
import com.techzo.cambiazo.ui.theme.CambiazoTheme
import com.techzo.cambiazo.ui.theme.ScreenBackground
import androidx.compose.ui.graphics.toArgb
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navViewModel: NavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        navViewModel = ViewModelProvider(this)[NavigationViewModel::class.java]

        handlePayPalIntent(intent)

        setContent {
            CambiazoTheme {
                val backgroundColor = ScreenBackground
                window.statusBarColor = backgroundColor.toArgb()
                WindowInsetsControllerCompat(
                    window,
                    window.decorView
                ).isAppearanceLightStatusBars = true

                NavScreen(
                    activity = this,
                    navViewModel = navViewModel
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePayPalIntent(intent)
    }

    private fun handlePayPalIntent(intent: Intent?) {
        val data = intent?.data ?: return

        if (data.scheme == "com.techzo.cambiazo" && data.host == "paypalpay") {
            val token = data.getQueryParameter("token") ?: return

            val subscriptionViewModel =
                ViewModelProvider(this)[SubscriptionViewModel::class.java]

            subscriptionViewModel.captureOrder(token) {
                navViewModel.triggerRedirectToSubscription()
            }
        }
    }
}
