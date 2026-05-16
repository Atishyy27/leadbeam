package com.fieldflow.feature.auth.domain.repository

import com.fieldflow.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getActiveUser(): Flow<User?>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun isUserLoggedIn(): Boolean
}