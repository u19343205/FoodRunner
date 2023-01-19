package com.codewithharsh.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.codewithharsh.foodrunner.R


class MyProfileFragment : Fragment() {

    private lateinit var imgUserImage: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvEmailAddress: TextView
    private lateinit var tvDeliveryAddress: TextView

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_my_profile2, container, false)
        sharedPreferences =
            (activity as FragmentActivity).getSharedPreferences("FoodRunner", Context.MODE_PRIVATE)

        imgUserImage = view.findViewById(R.id.imgUserImage)
        tvName = view.findViewById(R.id.tvName)
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber)
        tvEmailAddress = view.findViewById(R.id.tvEmailAddress)
        tvDeliveryAddress = view.findViewById(R.id.tvDeliveryAddress)

        tvName.text = sharedPreferences.getString("user_name", null)
        val phoneText = "+91 ${sharedPreferences.getString("user_mobile_number", null)}"
        tvPhoneNumber.text = phoneText
        tvEmailAddress.text = sharedPreferences.getString("user_email", null)
        tvDeliveryAddress.text = sharedPreferences.getString("user_address", null)
        return view
    }
}