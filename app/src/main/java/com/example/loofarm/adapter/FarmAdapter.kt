package com.example.loofarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loofarm.databinding.ItemFarmBinding

class FarmAdapter(private val dataSet: List<String>): RecyclerView.Adapter<ItemViewHolder>() {

    var onItemClick: ((Int)->Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = ItemFarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setDataItem(dataSet[position])
        holder.itemView.setOnClickListener{
            onItemClick?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

}