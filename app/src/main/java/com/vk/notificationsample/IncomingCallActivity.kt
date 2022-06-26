package com.vk.notificationsample

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vk.notificationsample.MainActivity.vib.vibrator
import com.vk.notificationsample.databinding.ActivityIncommingCallBinding

class IncomingCallActivity : AppCompatActivity() {
    private var binding: ActivityIncommingCallBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncommingCallBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        init()
        handleEvents()
    }

    private fun init() {
        vibrator.cancel()
        val notificationId = intent.getIntExtra("notification_id", 0)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
        val data = intent.getStringExtra("data")
        binding?.message?.text = data.toString()
    }

    private fun handleEvents() {

        binding?.btnAcceptCall?.setOnClickListener {
            shortToast("Call Accepted")
        }
        binding?.btnEndCall?.setOnClickListener {
            shortToast("Call Ended")
        }
    }


}