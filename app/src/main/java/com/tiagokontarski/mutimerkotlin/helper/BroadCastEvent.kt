package com.tiagokontarski.mutimerkotlin.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.annotation.MainThread
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.tiagokontarski.mutimerkotlin.R
import com.tiagokontarski.mutimerkotlin.activitys.MainActivity
import java.lang.Exception

class BroadCastEvent: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null && intent != null) {
            setNotification(context, intent = Intent(context, MainActivity::class.java), tick = "Evento!", title = "Mu Slow Event!", description = "An event begin, let's hunt!")
        }
}

    fun setNotification(context: Context, intent: Intent, tick: CharSequence, title: CharSequence, description: CharSequence) {

        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var p = PendingIntent.getActivity(context, 0, intent, 0)


        val mBuilder = NotificationCompat.Builder(context, "100")
        mBuilder.setContentIntent(p)
            .setTicker(tick)
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_action_logo)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_newlogo))
            .setAutoCancel(true)

        var n: Notification = mBuilder.build()
        n.vibrate
        n.flags = Notification.FLAG_AUTO_CANCEL
        mNotificationManager.notify(R.mipmap.ic_launcher_newlogo, n)

        try {

            val som: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            var toque: Ringtone = RingtoneManager.getRingtone(context, som)
            toque.play()

        }
        catch (e: Exception){}
    }



}