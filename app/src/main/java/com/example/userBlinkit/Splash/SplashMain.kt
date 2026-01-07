package com.example.userBlinkit.Splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.blinklit.R
import com.example.userBlinkit.Authentication.Signin
import com.example.userBlinkit.Authentication.Signup

class SplashMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_main)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this , Signup::class.java))
            finish()
        },4000)
    }
    }
