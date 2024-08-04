import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.loofarm.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight

class CustomMarkerView(
    context: Context,
    layoutResource: Int,
    private val timeStamps: List<String> // Thay bằng formattedTimeStamps
) : MarkerView(context, layoutResource) {
    private val tvContent: TextView = findViewById(R.id.tvContent)

    // Hiển thị thông tin cho điểm dữ liệu được nhấn
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        val xIndex = e?.x?.toInt() ?: -1 // Lấy chỉ số X
        val valueY = e?.y ?: 0f // Giá trị Y

        // Lấy mốc thời gian tương ứng từ formattedTimeStamps
        val timeStamp = if (xIndex in timeStamps.indices) {
            timeStamps[xIndex] // Trả về chuỗi thời gian đã được định dạng
        } else {
            "N/A" // Nếu không có giá trị hợp lệ
        }

        // Cập nhật nội dung của MarkerView
        tvContent.text = "X: $timeStamp\nY: $valueY"
        super.refreshContent(e, highlight)
    }
}
