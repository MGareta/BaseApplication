package com.core.data.environment.model

sealed class Environment(open val apiUrl: String) {
    data class DEV(override val apiUrl: String = "http://dev.api.example.com") : Environment(apiUrl = apiUrl)

    data class UAT(override val apiUrl: String = "http://uat.api.example.com") : Environment(apiUrl = apiUrl)

    data class PROD(override val apiUrl: String = "http://api.example.com") : Environment(apiUrl = apiUrl)
}
