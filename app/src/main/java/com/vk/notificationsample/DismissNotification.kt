package com.vk.notificationsample

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vk.notificationsample.MainActivity.vib.vibrator


class DismissNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        vibrator.cancel()
        val notificationId = intent.getIntExtra("notification_id", 0)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }


}