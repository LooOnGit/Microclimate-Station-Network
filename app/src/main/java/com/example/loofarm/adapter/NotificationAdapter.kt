package com.example.loofarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loofarm.databinding.ItemNotificationBinding

class NotificationAdapter(private val dataSet: List<String>): RecyclerView.Adapter<ItemViewNotifycationHolder>() {
    private var onItemClick: ((Int)->Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewNotifycationHolder {
        val view = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewNotifycationHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewNotifycationHolder, position: Int) {
        holder.setDataItem(dataSet[position])
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}