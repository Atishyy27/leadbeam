// feature/route/src/main/java/com/fieldflow/feature/route/notification/RouteNotificationManager.kt
package com.fieldflow.feature.route.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        private const val CHANNEL_ID = "route_execution"
        private const val NOTIFICATION_ID = 1001
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Route Execution",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Ongoing route execution notifications"
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    fun updateRouteProgress(routeName: String, progress: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentTitle("Route: $routeName")
            .setContentText(progress)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
    
    fun cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }
}