package com.example.loofarm.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.example.loofarm.databinding.ItemFarmBinding

class ItemViewHolder(private val binding: ItemFarmBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun setDataItem(itemName: String) {
        binding.tvItemName.text = "$itemName farm"
        binding.tvItemType.text = "Cây trồng: Bưởi"
    }
}
