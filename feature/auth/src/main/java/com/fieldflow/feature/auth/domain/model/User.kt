package com.fieldflow.feature.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val company: String,
    val title: String,
    val territory: String
)