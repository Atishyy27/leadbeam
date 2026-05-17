package com.fieldflow.feature.map.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun LocationPermissionHandler(
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    
    // Check if we already have it
    val isAlreadyGranted = ContextCompat.checkSelfPermission(
        context, 
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            onPermissionResult(isGranted)
        }
    )

    LaunchedEffect(Unit) {
        if (isAlreadyGranted) {
            onPermissionResult(true)
        } else {
            // Ask for it
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}