package com.fieldflow.feature.auth.domain.usecase

import com.fieldflow.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class CheckSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}