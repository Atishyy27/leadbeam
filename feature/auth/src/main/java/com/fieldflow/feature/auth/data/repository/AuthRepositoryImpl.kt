package com.fieldflow.feature.auth.data.repository

import com.fieldflow.core.database.dao.UserDao
import com.fieldflow.core.database.entity.UserEntity
import com.fieldflow.core.datastore.PreferencesManager
import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.network.model.LoginRequest
import com.fieldflow.feature.auth.domain.model.User
import com.fieldflow.feature.auth.domain.repository.AuthRepository
import nobility.core.common.result.Result // If using custom wrapper, else standard Kotlin Result below
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
            val tokenData = response.data

            if (response.status == 200 && tokenData != null) {
                // 1. Secure token persistence inside DataStore
                preferencesManager.saveTokens(tokenData.accessToken, tokenData.refreshToken)

                // 2. Immediate sequence fetch of profile updates from server
                val profileResponse = apiService.getUserProfile()
                val profile = profileResponse.data

                if (profileResponse.status == 200 && profile != null) {
                    // 3. Atomically overwrite cache tracking boundaries
                    val userEntity = UserEntity(
                        id = profile.id,
                        email = profile.email,
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        company = profile.company,
                        title = profile.title,
                        territory = profile.territory
                    )
                    userDao.insertOrUpdateUser(userEntity)

                    Result.success(User(profile.id, profile.email, profile.firstName, profile.lastName, profile.company, profile.title, profile.territory))
                } else {
                    Result.failure(Exception(profileResponse.message))
                }
            } else {
                Result.failure(Exception(response.message))
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