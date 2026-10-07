package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class BackgroundAudioService : Service() {
    private val binder = LocalBinder()
    lateinit var musicController: BackgroundMusicController
        private set

    inner class LocalBinder : Binder() {
        fun getService(): BackgroundAudioService = this@BackgroundAudioService
    }

    override fun onCreate() {
        super.onCreate()
        musicController = BackgroundMusicController(applicationContext)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra("EXTRA_TRACK_TITLE") ?: "HX HHD Master"
        val notification = createNotification(title)
        try {
            startForeground(1001, notification)
        } catch (e: Exception) {
            // Android 14+ safe guard
        }
        return START_STICKY
    }

    private fun createNotification(trackTitle: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("HX HHD Studio Playing")
            .setContentText(trackTitle)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "HX HHD Background Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Foreground playback channel for continuous studio audio."
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        musicController.release()
    }

    companion object {
        const val CHANNEL_ID = "hx_hhd_audio_playback_channel"
    }
}
