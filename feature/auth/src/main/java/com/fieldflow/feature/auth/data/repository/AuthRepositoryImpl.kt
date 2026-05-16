package com.fieldflow.feature.auth.data.repository

import com.fieldflow.core.database.dao.UserDao
import com.fieldflow.core.database.entity.UserEntity
import com.fieldflow.core.database.PreferencesManager
import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.network.model.LoginRequest
import com.fieldflow.feature.auth.domain.model.User
import com.fieldflow.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val preferencesManager: PreferencesManager
) : AuthRepository {

    override fun getActiveUser(): Flow<User?> {
        return userDao.getActiveUserFlow().map { entity ->
            entity?.let {
                User(it.id, it.email, it.firstName, it.lastName, it.company, it.title, it.territory)
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            val apiResponse = response.body() // ✅ Get the ApiResponse wrapper
            val tokenData = apiResponse?.data // ✅ Now access .data

            if (apiResponse?.status == 200 && tokenData != null) {
                preferencesManager.saveTokens(tokenData.accessToken, tokenData.refreshToken)

                val profileResponse = apiService.getUserProfile()
                val profileApiResponse = profileResponse.body() // ✅ Get ApiResponse
                val profile = profileApiResponse?.data // ✅ Access .data

                if (profileApiResponse?.status == 200 && profile != null) {
                    val userEntity = UserEntity(
                        id = profile.id,
                        email = profile.email ?: "",
                        firstName = profile.firstName ?: "",
                        lastName = profile.lastName ?: "",
                        company = profile.company ?: "",
                        title = profile.title ?: "",
                        territory = profile.territory ?: ""
                    )
                    userDao.insertOrUpdateUser(userEntity)

                    Result.success(User(
                        id = profile.id,
                        email = profile.email ?: "",
                        firstName = profile.firstName ?: "",
                        lastName = profile.lastName ?: "",
                        company = profile.company ?: "",
                        title = profile.title ?: "",
                        territory = profile.territory ?: ""
                    ))
                } else {
                    Result.failure(Exception(profileApiResponse?.message ?: "Profile fetch failed"))
                }
            } else {
                Result.failure(Exception(apiResponse?.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            preferencesManager.clearTokens()
            userDao.clearUserTable()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        val currentToken = preferencesManager.getAccessToken()
        return !currentToken.isNullOrBlank()
    }
}