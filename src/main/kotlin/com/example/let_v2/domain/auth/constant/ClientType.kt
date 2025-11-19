package com.example.let_v2.domain.auth.constant

object ClientType {
    const val WEB = "web"
    const val APP = "app"

    private val VALID_TYPES = setOf(WEB, APP)

    fun isValid(type: String): Boolean {
        return VALID_TYPES.contains(type.lowercase())
    }

    fun isApp(type: String): Boolean {
        return type.lowercase() in setOf(APP)
    }

    fun isWeb(type: String): Boolean {
        return type.lowercase() == WEB
    }
}