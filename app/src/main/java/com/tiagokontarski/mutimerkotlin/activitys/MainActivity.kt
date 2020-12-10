package com.tiagokontarski.mutimerkotlin.activitys

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.Toolbar
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.tiagokontarski.mutimerkotlin.R
import com.tiagokontarski.mutimerkotlin.adapterhelper.RecyclerAdapter
import com.tiagokontarski.mutimerkotlin.helper.DataHandler
import org.json.JSONObject

class MainActivity : AppCompatActivity(), RecyclerAdapter.onOptionsMenuClickListener {

    lateinit var toolbar: Toolbar
    lateinit var requestQueue: RequestQueue
    lateinit var rv: RecyclerView
    lateinit var adapter: RecyclerAdapter
    lateinit var alarm: AlarmManager
    lateinit var muteBtn: AppCompatButton
    lateinit var unMuteBtn: AppCompatButton

    var list: MutableList<DataHandler> = emptyList<DataHandler>().toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        requestQueue = Volley.newRequestQueue(this)

        rv = findViewById(R.id.recyclerview)
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        muteBtn = findViewById(R.id.mutebtn)
        unMuteBtn = findViewById(R.id.unmutebtn)

        muteBtn.setOnClickListener {
            for (i in 0 until list.size) {
                val intent = Intent("EVENT_OPEN")
                val pIntent = PendingIntent.getBroadcast(applicationContext, i, intent, 0)

                val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarm.cancel(pIntent)
            }
            Toast.makeText(applicationContext, "Todos os Alarmes desativados!", Toast.LENGTH_SHORT).show()
        }

            unMuteBtn.setOnClickListener {
                for (i in 0 until list.size){
                    val data = list.get(i)

                        if (alarmState(i)){
                            alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            val pIntent = PendingIntent.getBroadcast(applicationContext, i, intent, 0)
                            alarm.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis() + (data.timeLeft * 1000),
                                data.offset,
                                pIntent
                            )
                    }
                }
                Toast.makeText(applicationContext, "Todos os alarmes ativados!", Toast.LENGTH_SHORT).show()
            }

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = RecyclerAdapter(this, list)
        rv.adapter = adapter

        load()
    }

    fun load(){

        val request = JsonObjectRequest(
            Request.Method.GET,
            "https://www.muslow.net/api/events.php",
            null,
            Response.Listener<JSONObject> {

                list.clear()
                val names = it.names()

                if (names == null){
                    return@Listener
                }

                Log.i("Json", "JSON: $names")
                val intent = Intent("EVENT_OPEN")


                for (i in 0 until names.length()){

                    val key = names.getString(i)
                    val dados = it.getJSONObject(key)
                    list.add(DataHandler(
                        this,
                        i,
                        dados.getString("event"),
                        dados.getInt("opentime"),
                        dados.getInt("duration"),
                        dados.getString("last"),
                        dados.getString("next"),
                        dados.getString("nextF"),
                        dados.getLong("offset"),
                        dados.getLong("timeleft"),
                        adapter))

                    var alarmeAtivo: Boolean = PendingIntent
                        .getBroadcast(
                            this,
                            i,
                            Intent("EVENT_OPEN"),
                            PendingIntent.FLAG_NO_CREATE) == null

                    if (alarmeAtivo) {
                        alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val pIntent = PendingIntent.getBroadcast(this, i, intent, 0)
                        alarm.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + (dados.getLong("timeleft") * 1000),
                            dados.getLong("offset"),
                            pIntent
                        )
                    }
                    /*
                        val intent = Intent("EVENT_OPEN")
                        val pIntent = PendingIntent.getBroadcast(this, i, intent, 0)

                        val alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarm.cancel(pIntent)
                     */
                }

                adapter.notifyDataSetChanged()

            },
            null)
        requestQueue.add(request)
    }

    fun alarmState (identifier: Int) : Boolean {
        return PendingIntent
            .getBroadcast(
                this,
                identifier,
                Intent("EVENT_OPEN"),
                PendingIntent.FLAG_NO_CREATE
            ) == null
    }

    override fun onOptionsMenuClickListener(int: Int?, identifier: Int?) {

        Toast.makeText(applicationContext, "onOptionsMenuClick", Toast.LENGTH_SHORT).show()

        if (identifier != null) {
            val clickedItem = list.get(identifier)
            val eventName = clickedItem.eventName
            val id = clickedItem.id

            when (int) {
                R.id.NOTIFY_ON -> {

                    Toast.makeText(
                        applicationContext,
                        "Alarme para $eventName Ativado!",
                        Toast.LENGTH_SHORT
                    ).show()

                        if (alarmState(id)) {
                            alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            val pIntent =
                                PendingIntent.getBroadcast(applicationContext, id, intent, 0)
                            alarm.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis() + (clickedItem.timeLeft * 1000),
                                clickedItem.offset,
                                pIntent
                            )
                        }
                }
                R.id.NOTIFY_OFF -> {
                        Toast.makeText(
                            applicationContext,
                           "Alarme para $eventName Desativado!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent("EVENT_OPEN")
                        val pIntent =
                            PendingIntent.getBroadcast(applicationContext, identifier, intent, 0)

                        val alarm =
                            this@MainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        alarm.cancel(pIntent)
                }
            }
        }
    }
}
