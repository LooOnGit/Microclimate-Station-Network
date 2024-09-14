import android.content.Context
import android.graphics.Canvas
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

    // Điều chỉnh vị trí của MarkerView để không vượt quá màn hình
    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
        var x = posX
        var y = posY

        // Điều chỉnh lại vị trí x và y nếu MarkerView vượt quá màn hình
        val width = width.toFloat()
        val height = height.toFloat()

        // Nếu vượt quá bên phải màn hình, điều chỉnh x
        if (x + width > canvas.width) {
            x = canvas.width - width
        }

        // Nếu vượt quá bên trái màn hình, điều chỉnh x
        if (x < 0) {
            x = 0f
        }

        // Nếu vượt quá chiều cao màn hình, điều chỉnh y
        if (y + height > canvas.height) {
            y = canvas.height - height
        }

        // Gọi phương thức draw của MarkerView với vị trí mới
        super.draw(canvas, x, y)
    }
}

