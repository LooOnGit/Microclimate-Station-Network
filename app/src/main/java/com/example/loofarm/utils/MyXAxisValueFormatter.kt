package com.example.loofarm.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class MyXAxisValueFormatter(private val time: List<String?>?) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return time?.getOrNull(value.toInt()).formatTime()
    }
}
