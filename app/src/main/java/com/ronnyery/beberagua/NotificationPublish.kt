package com.ronnyery.beberagua

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat

class NotificationPublish: BroadcastReceiver()
{

    companion object{
         val KEY_NOTIFICATION_ID: String? = "key_notification_id"
         val KEY_NOTIFICATION: String? = "key_notification"
    }


    override fun onReceive(context: Context?, intent: Intent?)
    {

        val ii = Intent(context?.applicationContext,MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(context,0,ii,0)

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val message = intent?.getStringExtra(KEY_NOTIFICATION)
        val id = intent?.getIntExtra(KEY_NOTIFICATION_ID, 0)



        val notification = getNotification(message!!,context,notificationManager,pIntent)

        notificationManager.notify(id!!,notification)


    }


    private fun getNotification(content: String,
                                context: Context,
                                manager: NotificationManager,
                                intent: PendingIntent): Notification
    {
        val builder = NotificationCompat.Builder(context.applicationContext)
            .setContentText(content)
            .setContentIntent(intent)
            .setTicker("Alerta")
            .setAutoCancel(false)
            .setSmallIcon(R.mipmap.ic_launcher)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channelId = "id_chanel"
            val channel = NotificationChannel(channelId,"Notification name",NotificationManager.IMPORTANCE_DEFAULT)

            manager.createNotificationChannel(channel)

            builder.setChannelId(channelId)
        }

        return  builder.build()
    }
}