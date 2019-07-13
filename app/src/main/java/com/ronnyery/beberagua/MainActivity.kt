package com.ronnyery.beberagua

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {

        private const val  KEY_NOTIFY: String = "notify"
        private const val  KEY_INTERVAL: String = "key_interval"
        private const val  KEY_HOUR: String = "key_hour"
        private const val  KEY_MINUTE: String = "key_minute"



    }

    private var actived: Boolean = false
    private var minute: Int = 0
    private var hour: Int = 0
    private var interval: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val storage : SharedPreferences = getSharedPreferences("storage",Context.MODE_PRIVATE)

        val edit = storage.edit()

        actived = storage.getBoolean(KEY_NOTIFY,false)


        if(actived)
        {
            btnNotify.setText(R.string.pause)

            btnNotify.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))

            editNumberInteger.setText(storage.getInt(KEY_INTERVAL,0).toString())

            timePiker.currentHour  = storage.getInt(KEY_HOUR,timePiker.currentHour)

            timePiker.currentMinute = storage.getInt(KEY_MINUTE,timePiker.currentMinute)



        }
        else
        {
            btnNotify.setText(R.string.notify)

            btnNotify.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        }

        timePiker.setIs24HourView(true)

        btnNotify.setOnClickListener {


            val sInteval = editNumberInteger.text.toString()


            if (sInteval.isEmpty())
            {
                Toast.makeText(this, getString(R.string.validation), Toast.LENGTH_LONG).show()

                return@setOnClickListener
            }


            if (!actived) {
                interval = sInteval.toInt()

                hour = timePiker.currentHour

                minute = timePiker.currentMinute

                btnNotify.setText(R.string.pause)

                btnNotify.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))

                Log.d("DATE", "$interval $hour $minute")

                edit.putBoolean(KEY_NOTIFY, true)
                edit.putInt(KEY_INTERVAL,interval)
                edit.putInt(KEY_HOUR,hour)
                edit.putInt(KEY_MINUTE,minute)
                edit.apply()



                val calendar = Calendar.getInstance()

                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                //calendar.set(Calendar.SECOND,0)


                val notificationIntent = Intent(this,NotificationPublish::class.java)

                notificationIntent.putExtra(NotificationPublish.KEY_NOTIFICATION,1)
                notificationIntent.putExtra(NotificationPublish.KEY_NOTIFICATION, "Beber agua")

                val broadcast = PendingIntent.getBroadcast(this,0,notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT)

              //  val futureInMillis  = SystemClock.elapsedRealtime() + (interval * 1000)

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, broadcast)

                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,(interval * 60 * 1000).toLong(), broadcast)

                alarmManager.cancel(broadcast)

                actived = true


            }
            else
            {

                edit.putBoolean(KEY_NOTIFY, false)
                edit.remove(KEY_INTERVAL)
                edit.remove(KEY_HOUR)
                edit.remove(KEY_MINUTE)
                edit.apply()
                btnNotify.setText(R.string.notify)

                btnNotify.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))

                val notificationIntent = Intent(this,NotificationPublish::class.java)

                val broadcast = PendingIntent.getBroadcast(this,0,notificationIntent,
                    0)

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


                actived = false

            }


        }

    }
}
