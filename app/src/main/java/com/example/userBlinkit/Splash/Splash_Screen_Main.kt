package com.example.userBlinkit.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blinklit.R
import com.example.userBlinkit.Authentication.Signup

class Splash_Screen_Main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen_main)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, Signup::class.java))
            overridePendingTransition(R.anim.slide_in_right,0)
            finish()
        },3000)
    }
}