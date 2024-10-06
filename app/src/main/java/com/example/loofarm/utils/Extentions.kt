package com.example.loofarm.utils

import android.annotation.SuppressLint
import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

@SuppressLint("NewApi")
fun String?.formatTime(): String {
    if (this.isNullOrEmpty()) return "Unknown"
    // Định dạng chuỗi ISO 8601 thành LocalDateTime
    val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    // Chuyển đổi chuỗi thành LocalDateTime
    val localDateTime = LocalDateTime.parse(this, formatterInput)

    // Định dạng LocalDateTime thành chỉ giờ, phút, giây
    val formatterOutput = DateTimeFormatter.ofPattern("HH:mm:ss")
    return localDateTime.atOffset(ZoneOffset.UTC).format(formatterOutput)
}

@SuppressLint("NewApi")
fun String?.formatDate(): String {
    if (this.isNullOrEmpty()) return "Invalid Date"

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            // Chuyển đổi chuỗi ISO 8601 thành LocalDateTime
            val formatterInput = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val localDateTime = LocalDateTime.parse(this, formatterInput)

            // Định dạng ngày theo dd-MM-yyyy
            val formatterOutput = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            localDateTime.format(formatterOutput)

        } catch (e: Exception) {
            "Invalid Date"
        }
    } else {
        try {
            // Định dạng chuỗi trong môi trường Android cũ hơn
            val simpleDateFormatInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = simpleDateFormatInput.parse(this)

            val simpleDateFormatOutput = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            simpleDateFormatOutput.format(date)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}

// Extension function để tính thời gian sau một số ngày
@SuppressLint("NewApi")
fun String?.addDays(days: Long): String {
    if (this.isNullOrEmpty()) return "Invalid Date"
    val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    // Phân tích cú pháp chuỗi thời gian thành ZonedDateTime
    val initialDateTime = ZonedDateTime.parse(this, formatter)
    // Thêm số ngày vào thời gian ban đầu
    val newDateTime = initialDateTime.plus(days, ChronoUnit.DAYS)
    // Định dạng lại thời gian
    return newDateTime.format(formatter)
}
