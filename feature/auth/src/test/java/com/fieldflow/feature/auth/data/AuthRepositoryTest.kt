// feature/auth/src/test/java/com/fieldflow/feature/auth/data/AuthRepositoryTest.kt
package com.fieldflow.feature.auth.data

import com.fieldflow.core.datastore.TokenManager
import com.fieldflow.core.network.api.ApiService
import com.fieldflow.core.network.dto.LoginRequest
import com.fieldflow.core.network.dto.LoginResponse
import com.fieldflow.core.network.dto.TokenData
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthRepositoryTest {
    
    private lateinit var repository: AuthRepository
    private lateinit var apiService: ApiService
    private lateinit var tokenManager: TokenManager
    
    @Before
    fun setup() {
        apiService = mockk()
        tokenManager = mockk(relaxed = true)
        repository = AuthRepository(apiService, tokenManager)
    }
    
    @Test
    fun `login success saves tokens`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val response = LoginResponse(
            status = 200,
            message = "Success",
            data = TokenData(
                accessToken = "access_token",
                refreshToken = "refresh_token",
                expiresIn = 3600
            )
        )
        
        coEvery { apiService.login(LoginRequest(email, password)) } returns response
        
        // When
        val result = repository.login(email, password)
        
        // Then
        assertTrue(result is AuthResult.Success)
        coVerify { tokenManager.saveTokens("access_token", "refresh_token") }
    }
    
    @Test
    fun `login failure returns error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrong"
        
        coEvery { apiService.login(any()) } throws Exception("Invalid credentials")
        
        // When
        val result = repository.login(email, password)
        
        // Then
        assertTrue(result is AuthResult.Error)
        assertEquals("Invalid credentials", (result as AuthResult.Error).message)
    }
    
    @Test
    fun `isLoggedIn returns true when token exists`() = runTest {
        // Given
        coEvery { tokenManager.getAccessToken() } returns "valid_token"
        
        // When
        val result = repository.isLoggedIn()
        
        // Then
        assertEquals(true, result)
    }
}