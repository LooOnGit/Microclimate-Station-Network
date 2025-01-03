package com.example.loofarm.utils

import android.annotation.SuppressLint
import com.github.mikephil.charting.formatter.ValueFormatter

class DecimalValueFormatter : ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getFormattedValue(value: Float): String {
        return String.format("%.6f", value) // Hiển thị 6 chữ số thập phân
    }
}
