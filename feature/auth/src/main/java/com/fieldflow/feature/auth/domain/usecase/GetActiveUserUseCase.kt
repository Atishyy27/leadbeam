package com.fieldflow.feature.auth.domain.usecase

import com.fieldflow.feature.auth.domain.model.User
import com.fieldflow.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getActiveUser()
    }
}