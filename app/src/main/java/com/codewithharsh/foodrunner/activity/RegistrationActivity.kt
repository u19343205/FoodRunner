package com.codewithharsh.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.getSystemService
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.fragment.MyProfileFragment
import com.codewithharsh.foodrunner.util.ConnectionManager
import com.codewithharsh.foodrunner.util.Constraints
import com.codewithharsh.foodrunner.util.Preferences
import org.json.JSONException
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    private lateinit var registrationToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var edtName: EditText
    private lateinit var edtEmailAddress: EditText
    private lateinit var edtMobileNumber: EditText
    private lateinit var edtDeliveryAddress: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var preferences: Preferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        registrationToolbar = findViewById(R.id.registrationToolbar)
        setUpToolbar()
        edtName = findViewById(R.id.edtName)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtDeliveryAddress = findViewById(R.id.edtDeliveryAddress)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        preferences = Preferences(this@RegistrationActivity)
        sharedPreferences = this@RegistrationActivity.getSharedPreferences(
            preferences.PREF_NAME,
            preferences.PRIVATE_MODE
        )

        btnRegister.setOnClickListener() {

            if (Constraints.validateNameLength(edtName.text.toString())) {
                edtName.error = null  // for showing a visual error
                if (Constraints.validateEmailId(edtEmailAddress.text.toString())) {
                    edtEmailAddress.error = null
                    if (Constraints.validateMobile(edtMobileNumber.text.toString())) {
                        edtMobileNumber.error = null
                        if (Constraints.validatePasswordLength(edtPassword.text.toString())) {
                            edtPassword.error = null
                            if (Constraints.matchPassword(
                                    edtPassword.text.toString(),
                                    edtConfirmPassword.text.toString()
                                )
                            ) {
                                edtPassword.error = null
                                edtConfirmPassword.error = null
                                registerUser()
                            } else {
                                edtPassword.error = "Password don't match"
                                edtConfirmPassword.error = "Password don't match"
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Password don't match",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            edtPassword.error = "Password should be more than or equal to 4 digits"
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Password should be more than or equal to 4 digits",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        edtMobileNumber.error = "Invalid Mobile number"
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Invalid Mobile number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    edtEmailAddress.error = "Invalid Email"
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Invalid Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                edtName.error = "Invalid name"
                Toast.makeText(
                    this@RegistrationActivity,
                    "Invalid name",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun registerUser() {

        val name = edtName.text.toString()
        val emailAddress = edtEmailAddress.text.toString()
        val mobileNumber = edtMobileNumber.text.toString()
        val deliveryAddress = edtDeliveryAddress.text.toString()
        val password = edtPassword.text.toString()


        //Checking net connectivity
        if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
            //Volley
            val queue = Volley.newRequestQueue(this@RegistrationActivity)
            val url = "http://13.235.250.119/v2/register/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("name", name)
            jsonParams.put("email", emailAddress)
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("address", deliveryAddress)
            jsonParams.put("password", password)


            val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                Response.Listener {
                    println("Response is $it")
                    try {
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            val response = jsonObject.getJSONObject("data")

                            sharedPreferences.edit()
                                .putString("user_id", response.getString("user_id")).apply()
                            sharedPreferences.edit()
                                .putString("user_name", response.getString("name")).apply()
                            sharedPreferences.edit()
                                .putString("user_email", response.getString("email")).apply()
                            sharedPreferences.edit().putString(
                                "user_mobile_number",
                                response.getString("mobile_number")).apply()
                            sharedPreferences.edit()
                                .putString("user_address", response.getString("address")).apply()
                            preferences.setLogIn(true)
                            val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            val message : String = jsonObject.getString("errorMessage")
                            Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@RegistrationActivity, "Some Unexpected Error Occurred !!!!", Toast.LENGTH_SHORT).show()
                    }
                },

                Response.ErrorListener {
                    println("Error is $it")
                    Toast.makeText(this@RegistrationActivity,"Volley Error Occurred",Toast.LENGTH_SHORT).show()
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
            val dialog = AlertDialog.Builder(this@RegistrationActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection is not found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val openSettings = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(openSettings)
                finish()
            }
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(registrationToolbar)
        supportActionBar?.title = "Register Here"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)// for displaying the default icon
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}