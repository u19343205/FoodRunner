package com.codewithharsh.foodrunner.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.codewithharsh.foodrunner.R

class ProfileActivity : AppCompatActivity() {

    lateinit var imgUserImage: ImageView
    lateinit var tvName: TextView
    lateinit var tvPhoneNumber: TextView
    lateinit var tvEmailAddress: TextView
    lateinit var tvDeliveryAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgUserImage = findViewById(R.id.imgUserImage)
        tvName = findViewById(R.id.tvName)
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber)
        tvEmailAddress = findViewById(R.id.tvEmailAddress)
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress)

        if (intent != null) {
            tvName.text = intent.getStringExtra("name")
            tvPhoneNumber.text = intent.getStringExtra("phoneNumber")
            tvEmailAddress.text = intent.getStringExtra("emailAddress")
            tvDeliveryAddress.text = intent.getStringExtra("deliveryAddress")
        }
    }
}