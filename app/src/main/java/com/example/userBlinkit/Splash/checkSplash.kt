package com.example.userBlinkit.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blinklit.R
import com.example.userBlinkit.Home.Home

class checkSplash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Home::class.java))
            finish()
        },2000)
    }
}