package com.ronnyery.beberagua

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
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

    lateinit var storage : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = getSharedPreferences("storage",Context.MODE_PRIVATE)


        actived = storage.getBoolean(KEY_NOTIFY,false)

        setupUI(actived,storage)



        timePiker.setIs24HourView(true)

        btnNotify.setOnClickListener(notifyListener)

    }

    private fun alert(resId: Int)
    {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

    }

    private fun intervalIsValid(): Boolean
    {

        val sInteval = editNumberInteger.text.toString()

        if (sInteval.isEmpty())
        {
            alert(R.string.validation)

            return false
        }

        if (sInteval == "0")
        {
            alert(R.string.zero_value)
            return false
        }

        return true
    }

    private fun setupUI(activated: Boolean, storage: SharedPreferences)
    {

        if(activated)
        {
            btnNotify.setText(R.string.pause)

            btnNotify.setBackgroundResource(R.drawable.bg_button_background)

            editNumberInteger.setText(storage.getInt(KEY_INTERVAL,0).toString())

            timePiker.currentHour  = storage.getInt(KEY_HOUR,timePiker.currentHour)

            timePiker.currentMinute = storage.getInt(KEY_MINUTE,timePiker.currentMinute)

        }
        else
        {
            btnNotify.setText(R.string.notify)

            btnNotify.setBackgroundResource(R.drawable.bg_button_background_accent)
        }
    }

    private fun updateStorage(added:Boolean, interval: Int = 0, hour: Int = 0, minute: Int = 0) {
        val edit = storage.edit()

        edit.putBoolean(KEY_NOTIFY, added)

        if (added)
        {
            edit.putInt(KEY_INTERVAL, interval)
            edit.putInt(KEY_HOUR, hour)
            edit.putInt(KEY_MINUTE, minute)
        }
        else
        {
            edit.remove(KEY_INTERVAL)
            edit.remove(KEY_HOUR)
            edit.remove(KEY_MINUTE)
        }


        edit.apply()
    }

    private fun setupNOtification(added:Boolean, interval: Int=0, hour: Int=0, minute: Int=0)
    {

        val notificationIntent = Intent(this,NotificationPublish::class.java)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        if(added) {

            val calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND,0)


            notificationIntent.putExtra(NotificationPublish.KEY_NOTIFICATION, 1)
            notificationIntent.putExtra(NotificationPublish.KEY_NOTIFICATION, "Beber agua")

            val broadcast = PendingIntent.getBroadcast(
                this, 0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            //  val futureInMillis  = SystemClock.elapsedRealtime() + (interval * 1000)

            //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, broadcast)

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                (interval * 60 * 1000).toLong(),
                broadcast
            )

           // alarmManager.cancel(broadcast)
        }
        else
        {

            val broadcast = PendingIntent.getBroadcast(this,0,notificationIntent,
                0)

            alarmManager.cancel(broadcast)
        }

    }


        private val notifyListener: View.OnClickListener = View.OnClickListener {




            if (!actived)
            {

                if (!intervalIsValid()) return@OnClickListener

                val interval:Int = editNumberInteger.text.toString().toInt()
                val hour:Int = timePiker.currentHour
                val minute:Int = timePiker.currentMinute

                updateStorage(true,interval, hour, minute)
                setupUI(true,storage)
                setupNOtification(true,interval,hour,minute)
                alert(R.string.notified)

                actived = true

            }
            else
            {

                updateStorage(false)
                setupUI(false,storage)
                setupNOtification(false)
                alert(R.string.notified_pause)

                actived = false

            }


    }




}
