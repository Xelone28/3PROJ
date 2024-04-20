package com.console.ratcord.api

enum class LocalStorage(val value: String) {
    PREFERENCES_FILE_KEY("SecretSharedPrefs"),

    TOKEN_KEY("jwt_token"),
    USER("user"),
}