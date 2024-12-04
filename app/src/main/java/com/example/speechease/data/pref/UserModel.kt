package com.example.speechease.data.pref

data class UserModel(
    val userId: String? = null,
    val name: String? = null,
    val email: String? = null,
    val token: String? = null,
    val isLogin: Boolean = false
)