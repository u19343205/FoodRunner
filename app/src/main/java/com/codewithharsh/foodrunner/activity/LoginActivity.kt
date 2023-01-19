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
            loginUser()
        }


    }

    fun loginUser() {

        val mobileNumber = edtMobileNumber.text.toString()
        val password = edtPassword.text.toString()

        //  here we  have used the class COnstraints to check for validations
        if (Constraints.validateMobile(mobileNumber) && Constraints.validatePasswordLength(password)) {

            // here we are checking for internet connection
            if (ConnectionManager().checkConnectivity(this@LoginActivity)) {

                //Volley Request
                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = " http://13.235.250.119/v2/login/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", password)


                val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                    Response.Listener {
                        try {

                            val loginJsonObject = it.getJSONObject("data")
                            val success = loginJsonObject.getBoolean("success")
                            if (success) {

                                //  if success then we save the credentials in sharedpreferences
                                val response = loginJsonObject.getJSONObject("data")
                                sharedPreferences.edit()
                                    .putString("user_id", response.getString("user_id")).apply()
                                sharedPreferences.edit()
                                    .putString("user_name", response.getString("name")).apply()
                                sharedPreferences.edit()
                                    .putString("user_email", response.getString("email")).apply()
                                sharedPreferences.edit()
                                    .putString("user_mobile_number", response.getString("mobile_number")).apply()
                                sharedPreferences.edit()
                                    .putString("user_address", response.getString("address"))
                                    .apply()

                                preferences.setLogIn(true)
                                val intent  = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()

                            } else {
                                val message: String = loginJsonObject.getString("errorMessage")
                                Toast.makeText(
                                    this@LoginActivity,
                                    "$message ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }catch (e : JSONException){
                            e.printStackTrace()
                        }

                    },
                    Response.ErrorListener {
                        println("error is $it")

                        Toast.makeText(
                            this@LoginActivity,
                            "Some error has occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["Token"] = "6b26ac55c5e989"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }else {
                // if no internet connection found we go to WIFI settings
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet connection is not found")
                dialog.setPositiveButton("Open Settings") { _, _ ->
                    val openSettings = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(openSettings)
                    finish()
                }
            }
        }else{
// if ther is some problems in validating
            btnLogin.visibility = View.VISIBLE
            tvForgotPassword.visibility = View.VISIBLE
            tvSignUpNow.visibility = View.VISIBLE
            Toast.makeText(this@LoginActivity, "Invalid Number or Password", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

