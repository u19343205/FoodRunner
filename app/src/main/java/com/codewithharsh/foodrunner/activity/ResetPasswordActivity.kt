package com.codewithharsh.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.util.ConnectionManager
import com.codewithharsh.foodrunner.util.Constraints
import org.json.JSONException
import org.json.JSONObject
import java.util.Queue

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var edtOTP: EditText
    private lateinit var edtNewPassword: EditText
    private lateinit var edtConfirmNewPassword: EditText
    private lateinit var btnSubmit: Button
    private var mobileNumber: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        edtOTP = findViewById(R.id.edtOTP)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile").toString()
        }

        btnSubmit.setOnClickListener {

            var otp = edtOTP.text.length
            var newPassword = edtNewPassword.text.toString()
            var conNewPaassword = edtConfirmNewPassword.text.toString()

            if (otp == 4) {
                if (Constraints.validatePasswordLength(newPassword)) {
                    if (Constraints.matchPassword(newPassword, conNewPaassword)) {
                        sendOtp(mobileNumber, edtOTP.text.toString(), newPassword)
                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Password does not match",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Invalid Password",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } else {
                Toast.makeText(
                    this@ResetPasswordActivity,
                    "Incorrect OTP! Please enter the correct OTP",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sendOtp(mobileNumber: String, otp: String, password: String) {

        if (ConnectionManager().checkConnectivity(this@ResetPasswordActivity)) {
            val queue = Volley.newRequestQueue(this@ResetPasswordActivity)
            val url = "http://13.235.250.119/v2/reset_password/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", password)
            jsonParams.put("otp", otp)

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams,
                    Response.Listener {
                        try {
                            val reserObject = it.getJSONObject("data")
                            val success = reserObject.getBoolean("success")
                            if (success) {
                                val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
                                dialog.setTitle("Confirmation...")
                                dialog.setMessage("Your Your password has been changed")
                                dialog.setIcon(R.drawable.ic_confirmed)
                                dialog.setCancelable(false)
                                dialog.setPositiveButton("ok") { _, _ ->
                                    val intent = Intent(
                                        this@ResetPasswordActivity,
                                        LoginActivity::class.java
                                    )
                                    startActivity(intent)
                                    ActivityCompat.finishAffinity(this@ResetPasswordActivity)
                                }
                                dialog.create()
                                dialog.show()
                            } else {
                                val error = reserObject.getString("errorMessage")
                                Toast.makeText(
                                    this@ResetPasswordActivity,
                                    error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener {
                        VolleyLog.e("Error:::::", "/Post request fail error: ${it.message}")
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "${it.message}",
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
        }else{
            val dialog = AlertDialog.Builder(this@ResetPasswordActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                val openSettings = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(openSettings)
            }
            dialog.setNegativeButton("Cancle"){ _, _ ->
                //Do Nothing
            }
            dialog.create()
            dialog.show()
        }
    }
}
