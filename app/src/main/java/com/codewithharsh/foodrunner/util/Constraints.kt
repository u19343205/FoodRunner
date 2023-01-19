package com.codewithharsh.foodrunner.util

import android.util.Patterns

object Constraints {

    fun validateNameLength(name: String): Boolean {
        return name.length >= 3
    }

    fun validateEmailId(email: String): Boolean {
        return (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    fun validateMobile(mobile: String): Boolean {
        return mobile.length == 10
    }

    fun validatePasswordLength(passwrod: String): Boolean {
        return passwrod.length >= 4
    }

    fun matchPassword(pass: String, confirmPass: String): Boolean {
        return pass == confirmPass
    }


}