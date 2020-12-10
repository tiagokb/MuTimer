package com.tiagokontarski.mutimerkotlin.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import com.tiagokontarski.mutimerkotlin.activitys.MainActivity
import com.tiagokontarski.mutimerkotlin.adapterhelper.RecyclerAdapter

data class DataHandler (
    val context: Context,
    var id: Int,
    var eventName: String,
    var opentime: Int,
    var duration: Int,
    var last: String,
    var next: String,
    var eventDate: String,
    var offset: Long,
    var timeLeft: Long,
    val adapter: RecyclerAdapter){

    fun eventOpen(int1: Int, int2: Int): Long{
        var isEventOpen: Long
        if (int1 == 0) {
            isEventOpen = secondsToMilliseconds(int2.toLong())
            return isEventOpen
        }else{
            isEventOpen = secondsToMilliseconds(int1.toLong())
            return isEventOpen
        }
    }

    var newCountDownTimer: CountDownTimer
    var currentVal: String = "00:00:00"

    init {
        newCountDownTimer = object : CountDownTimer(secondsToMilliseconds(timeLeft), 1000) {
            override fun onTick(millisUntilFinished: Long) {

                val isEventOpen: Long = secondsToMilliseconds(offset) - eventOpen(opentime, duration)
                if (millisUntilFinished > isEventOpen){
                    currentVal = "In progress"
                    adapter.notifyItemChanged(id)
                }else {
                    var seconds = (millisUntilFinished / 1000).toInt()
                    val hours = seconds / (60 * 60)
                    val tempMint = seconds - hours * 60 * 60
                    val minutes = tempMint / 60
                    seconds = tempMint - minutes * 60
                    currentVal =
                        String.format("%02d", hours) + ":" + String.format(
                            "%02d",
                            minutes
                        ) + ":" + String.format("%02d", seconds)
                    adapter.notifyItemChanged(id)
                }
            }
            override fun onFinish() {
                onTick(offset * 1000)
                adapter.notifyItemChanged(id)
            }
        }.start()
    }

    fun secondsToMilliseconds(parameter: Long): Long{
        var milli: Long = parameter * 1000
        return milli
    }
}