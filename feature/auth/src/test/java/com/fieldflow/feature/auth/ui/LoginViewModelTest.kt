// feature/auth/src/test/java/com/fieldflow/feature/auth/ui/LoginViewModelTest.kt
package com.fieldflow.feature.auth.ui

import com.fieldflow.feature.auth.data.AuthRepository
import com.fieldflow.feature.auth.data.AuthResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    
    private lateinit var viewModel: LoginViewModel
    private lateinit var repository: AuthRepository
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = LoginViewModel(repository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `login success updates state to success`() = runTest {
        // Given
        coEvery { repository.login(any(), any()) } returns AuthResult.Success
        
        // When
        viewModel.updateEmail("test@example.com")
        viewModel.updatePassword("password123")
        viewModel.login()
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Success)
    }
    
    @Test
    fun `login shows loading state during execution`() = runTest {
        // Given
        coEvery { repository.login(any(), any()) } coAnswers {
            kotlinx.coroutines.delay(100)
            AuthResult.Success
        }
        
        // When
        viewModel.login()
        
        // Then
        assertTrue(viewModel.uiState.value is LoginUiState.Loading)
    }
    
    @Test
    fun `invalid email shows error`() = runTest {
        // When
        viewModel.updateEmail("invalid")
        viewModel.updatePassword("password123")
        viewModel.login()
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state is LoginUiState.Error)
    }
}