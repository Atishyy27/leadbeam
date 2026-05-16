package com.fieldflow.feature.auth.domain.usecase

import com.fieldflow.feature.auth.domain.model.User
import com.fieldflow.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || !email.contains("@")) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }
        return authRepository.login(email, password)
    }
}