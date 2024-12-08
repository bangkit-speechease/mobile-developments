package com.example.speechease.data.pref

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import androidx.core.content.edit as editSP

class UserPreference private constructor(private val context: Context) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences("session", MODE_PRIVATE)
    }

    suspend fun saveSession(user: UserModel) {
        withContext(Dispatchers.IO) {
            sharedPreferences.editSP {
                putString(USER_ID_KEY, user.userId)
                putString(NAME_KEY, user.name)
                putString(EMAIL_KEY, user.email)
                putString(TOKEN_KEY, user.token)
                putBoolean(IS_LOGIN_KEY, user.isLogin)
            }
        }
        Log.d("UserPreference", "Token disimpan ke SharedPreferences: ${user.token}")
    }

    fun getSession(): Flow<UserModel> {
        return flow {
            val userModel = UserModel(
                sharedPreferences.getString(USER_ID_KEY, "") ?: "",
                sharedPreferences.getString(NAME_KEY, "") ?: "",
                sharedPreferences.getString(EMAIL_KEY, "") ?: "",
                sharedPreferences.getString(TOKEN_KEY, "") ?: "",
                sharedPreferences.getBoolean(IS_LOGIN_KEY, false)
            )
            Log.d("User Preference", "Token diambil dari SharedPreferences: ${userModel.token}")
            emit(userModel)
        }
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            sharedPreferences.editSP { clear() }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserPreference? = null

        private const val USER_ID_KEY = "userId"
        private const val NAME_KEY = "name"
        private const val EMAIL_KEY = "email"
        private const val TOKEN_KEY = "token"
        private const val IS_LOGIN_KEY = "isLogin"

        fun getInstance(context: Context): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(context)
                INSTANCE = instance
                instance
            }
        }
    }
}