package com.example.loofarm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loofarm.databinding.ItemFarmBinding
import com.example.loofarm.model.ThingSpeakResponse

class FarmAdapter(
    private val thingSpeakResponse: ThingSpeakResponse?,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = ItemFarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $thingSpeakResponse")
        holder.setDataItem(thingSpeakResponse?.channel?.name ?: "")
        holder.itemView.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: click // $position")
            listener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    companion object {
        private val TAG by lazy { FarmAdapter::class.java.simpleName }
    }
}
