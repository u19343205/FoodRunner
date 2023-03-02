package com.codewithharsh.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.util.ConnectionManager
import com.codewithharsh.foodrunner.util.Constraints
import com.codewithharsh.foodrunner.util.Preferences
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var edtMobileNumber: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSignUpNow: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferences = Preferences(this@LoginActivity)
        sharedPreferences =
            this.getSharedPreferences(preferences.PREF_NAME, preferences.PRIVATE_MODE)

        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        tvSignUpNow = findViewById(R.id.tvSignUpNow)

// if the user clicks on SignUpNow button then he gets directed to registration activity
        tvSignUpNow.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

// if the user clicked on forgot password then he gets directed to forgot password activity
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

// if the user clicks on login button - if the credentials entered are correct the user is directed to home page of te app
        btnLogin.setOnClickListener {
            // Move to homepage
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() //
        }
    }
}


