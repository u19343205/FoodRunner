package com.codewithharsh.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
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

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var edtMobileNumber: EditText
    lateinit var edtEmailAddress: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {

            val mobileNumber = edtMobileNumber.text.toString()
            val emailAddress = edtEmailAddress.text.toString()
            if (Constraints.validateNameLength(mobileNumber)) {
                edtMobileNumber.error = null
                if (Constraints.validateEmailId(emailAddress)) {
                    forgotPasswordOtp(mobileNumber, emailAddress)
                } else {
                    edtEmailAddress.error = "Invalid Email"
                }
            } else {
                edtMobileNumber.error = "Invalid Mobile Number"
            }
        }
    }

    fun forgotPasswordOtp(mobileNumber: String, emailAddress: String) {

        if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {

            //Volley Request
            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("email", emailAddress)

            val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val firstTry = data.getBoolean("first_try")
                            if (firstTry) {
                                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                                dialog.setTitle("Information")
                                dialog.setMessage("Please check You registered email-Id")
                                dialog.setCancelable(false)
                                dialog.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intent.putExtra("user_mobile", mobileNumber)
                                    startActivity(intent)
                                }
                                dialog.create()
                                dialog.show()
                            } else {
                                val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                                dialog.setTitle("Information")
                                dialog.setMessage("Please refer to the previous email for the OTP")
                                dialog.setCancelable(false)
                                dialog.setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(
                                        this@ForgotPasswordActivity,
                                        ResetPasswordActivity::class.java
                                    )
                                    intent.putExtra("user_mobile", mobileNumber)
                                    startActivity(intent)
                                }
                                dialog.create()
                                dialog.show()
                            }
                        } else {
                            Toast.makeText(
                                this@ForgotPasswordActivity,
                                "Mobile number not registered!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "Incorrect response error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    VolleyLog.e("Error:::::", "/Post request fail error: ${it.message}")
                    Toast.makeText(this@ForgotPasswordActivity, "${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["Token"] = "6b26ac55c5e989"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            // if no internet connection found we go to WIFI settings
            val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found...")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val openSettings = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(openSettings)
            }
            dialog.setNegativeButton("cancle") { _, _ ->
                //Do Nothing
            }
            dialog.create()
            dialog.show()
        }
    }
}




























