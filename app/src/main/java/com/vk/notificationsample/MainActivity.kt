package com.vk.notificationsample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioManager.STREAM_RING
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.vk.notificationsample.MainActivity.vib.vibrator

class MainActivity : AppCompatActivity() {
    private val isHeadsetPlugged: Boolean = false
    private var startedRinging = false
    private val CHANNEL_ID = "1channelId"
    private val defaultRingtone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

    object vib {
        lateinit var vibrator: Vibrator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btClick).setOnClickListener {
            showNotificationWithFullScreenIntent()
        }
    }


    private fun vibratePhone(context: Context, duration: Long) {
        vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(longArrayOf(0, duration, 500), 0)
    }


    private fun Context.showNotificationWithFullScreenIntent() {
        val notificationLayout = RemoteViews(this.packageName, R.layout.custom_notification)

        val acceptIntent = Intent(this, IncomingCallActivity::class.java).apply {
            putExtra("notification_id", 8547)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val cancelIntent = Intent(this, DismissNotification::class.java).apply {
            putExtra("notification_id", 8547)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingAcceptIntent =
            PendingIntent.getActivity(this, 0, acceptIntent, PendingIntent.FLAG_IMMUTABLE)

        val pendingCancelIntent =
            PendingIntent.getBroadcast(this, 1, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

        notificationLayout.setOnClickPendingIntent(R.id.btnAnswer, pendingAcceptIntent)
        notificationLayout.setOnClickPendingIntent(R.id.btnDeclinee, pendingCancelIntent)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("title")
            .setContentText("description")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setCustomContentView(notificationLayout)
            .setVibrate(longArrayOf(0))
            .setSound(defaultRingtone, STREAM_RING)
            .setContentIntent(pendingAcceptIntent)
            .setFullScreenIntent(pendingAcceptIntent, true)
            .setAutoCancel(true)
            .setTimeoutAfter(20000)
            .setDeleteIntent(pendingCancelIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationManager) {
            buildChannel()

            val notification = builder.build()

            notify(8547, notification)
        }
        vibratePhone(this@MainActivity, 700)
        //  startRingtoneAndVibration()
    }

    private fun NotificationManager.buildChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //customRingtone = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.sample)
            val name = "your channel name"
            val descriptionText = "This channel is used for sample channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(false)
                enableVibration(false)
                setBypassDnd(true)
                setSound(
                    defaultRingtone,
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setLegacyStreamType(STREAM_RING) // if headphones are plugged  AudioManager.STREAM_VOICE_CALL todo
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )
            }
            createNotificationChannel(channel)
        }
    }


    fun Context.startRingtoneAndVibration() {
        if (!startedRinging) {
            startedRinging = true;

            val am = getSystemService(AUDIO_SERVICE) as AudioManager
            val needRing = am.ringerMode != AudioManager.RINGER_MODE_SILENT
            if (needRing) {
                val ringtonePlayer = MediaPlayer()
                ringtonePlayer.setOnPreparedListener { mediaPlayer ->
                    ringtonePlayer.start()
                }
                ringtonePlayer.isLooping = true;
                if (isHeadsetPlugged) {
                    ringtonePlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                } else {
                    ringtonePlayer.setAudioStreamType(STREAM_RING);
                    /*    if (!USE_CONNECTION_SERVICE) {
                            am.requestAudioFocus(
                                this,
                                AudioManager.STREAM_RING,
                                AudioManager.AUDIOFOCUS_GAIN
                            );
                        }*/
                }
                //  try {
                val notificationUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString()
                ringtonePlayer.setDataSource(this, Uri.parse(notificationUri));
                ringtonePlayer.prepareAsync();
                /*   } catch (e Exception) {
                       FileLog.e(e);
                       if (ringtonePlayer != null) {
                           ringtonePlayer.release();
                           ringtonePlayer = null;
                       }
                   }*/

                val vibrate = 0 //todo
                if ((vibrate != 2 && vibrate != 4 && (am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE || am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL)) || (vibrate == 4 && am.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)) {
                    val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    var duration = 700.toLong()
                    if (vibrate == 1) {
                        duration /= 2;
                    } else if (vibrate == 3) {
                        duration *= 2;
                    }
                    vibrator.vibrate(longArrayOf(0, duration, 500), 0)
                }
            }
        }
    }

}