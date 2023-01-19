package com.codewithharsh.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.util.Preferences

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        preferences = com.codewithharsh.foodrunner.util.Preferences(this)
        val background = object : Thread() {
            override fun run() {
                try {
                    sleep(1000)
                    if (preferences.isLoggedIn()) {
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}


//        Handler(Looper.getMainLooper()).postDelayed({
//            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, 1000)