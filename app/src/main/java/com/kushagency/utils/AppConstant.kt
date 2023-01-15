package com.kushagency.utils

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.fragment.app.Fragment


const val BASE_URL = "https://admin.ankitchawda.in/api"
//https://f592-2402-8100-387e-6d72-4c28-6656-1739-654f.ngrok.io/api

fun isValidVehicleNumber(value : String): Boolean{
    val VEHICLE_NUMBER_REGEX  = "^[a-zA-z]{2}[0-9]{2}[a-zA-z]{2}[0-9]{4}$"
    return value.matches(Regex(VEHICLE_NUMBER_REGEX))
}

fun Activity.showToast(msg : String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msg : String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}