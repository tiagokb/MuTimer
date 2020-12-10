package com.tiagokontarski.mutimerkotlin.adapterhelper

import android.content.Context
import android.net.sip.SipSession
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.tiagokontarski.mutimerkotlin.R
import com.tiagokontarski.mutimerkotlin.activitys.MainActivity
import com.tiagokontarski.mutimerkotlin.helper.DataHandler

class RecyclerAdapter(
    var context: Context,
    var list: List<DataHandler>): RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>(), PopupMenu.OnMenuItemClickListener {

    private var currentPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {

        var view = LayoutInflater.from(context).inflate(
            R.layout.recycler_item,
            parent,
            false
        )

        return RecyclerHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        val dataHandler = list[position]
        holder.setData(dataHandler)

        holder.timerOptions.setOnClickListener{
            popUpMenu(holder.itemView, position)
        }

        holder.apply {
            eventName.text = dataHandler.eventName
            eventDate.text = dataHandler.eventDate
        }
    }

    class RecyclerHolder(view: View) : RecyclerView.ViewHolder(view) {

        var eventName: AppCompatTextView = view.findViewById(R.id.event_name)
        var eventDate: AppCompatTextView = view.findViewById(R.id.event_date)
        var timer: AppCompatTextView = view.findViewById(R.id.countdown_timer)
        var timerOptions: AppCompatImageView = view.findViewById(R.id.popupmenu)


        fun setData(model: DataHandler) {
            timer.text = model.currentVal
        }
    }

    fun popUpMenu(view: View, position: Int) {
        PopupMenu(context, view).apply {
            setOnMenuItemClickListener(this@RecyclerAdapter)
            inflate(R.menu.popup_menu)
            currentPosition = position
            show()

        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.NOTIFY_ON -> {
                listener.onOptionsMenuClickListener(currentPosition, R.id.NOTIFY_ON)
                true
            }
            R.id.NOTIFY_OFF -> {
                listener.onOptionsMenuClickListener(currentPosition, R.id.NOTIFY_OFF)
                true
            }
            else -> false

        }
    }

    interface onOptionsMenuClickListener {
        fun onOptionsMenuClickListener(int: Int?, identifier: Int?)
    }

    val listener = object : onOptionsMenuClickListener{
        override fun onOptionsMenuClickListener(int: Int?, identifier: Int?) {
        }

    }



}
