// feature/profile/src/main/java/com/fieldflow/feature/profile/ui/ProfileScreen.kt
package com.fieldflow.feature.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fieldflow.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    authRepository: AuthRepository, // Pass this via DI/ViewModel in your NavHost
    onNavigateToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = {
                scope.launch {
                    authRepository.logout() // Clears DataStore and Room instantly
                    onNavigateToLogin()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}