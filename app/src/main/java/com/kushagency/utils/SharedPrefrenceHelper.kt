package com.kushagency.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.kushagency.KAPilotApplication

object SharedPrefrenceHelper {
    private const val LOGIN_STATUS = "LOGIN_STATUS"
    private const val SIGNUP_STATUS = "SIGNUP_STATUS"
    private const val INTRODUCTION = "INTRO"

    private const val USER = "USER"
    private const val USER_ID = "USER_ID"


    private val sharedPrefs: SharedPreferences

    init {
        val context = KAPilotApplication.instance
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var userId: String?
        get() {
            return sharedPrefs.getString(USER_ID, null)
        }
        set(value) {
            sharedPrefs.edit {
                putString(USER_ID, value)
            }
        }



/*
    var user: UserEntity
        set(value) = sharedPrefs.edit {
            val userJson = Gson().toJson(value)
            putString(USER, userJson)
            Log.d("Users_", userJson.toString())
        }
        get() {
            val userString = sharedPrefs.getString(USER, null) ?: throw Exception("User not Found")
            Log.d("Users_", userString)



            return  Gson().fromJson(userString, UserEntity::class.java)
        }
*/

}
