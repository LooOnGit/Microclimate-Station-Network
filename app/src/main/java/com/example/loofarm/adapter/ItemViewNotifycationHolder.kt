package com.example.loofarm.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.loofarm.databinding.ItemNotificationBinding

class ItemViewNotifycationHolder (private val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root) {
    fun setDataItem(txt: String) {
        binding.txtNotification.text = txt
    }
}