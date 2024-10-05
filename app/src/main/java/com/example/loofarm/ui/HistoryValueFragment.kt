package com.example.loofarm.ui

import CustomMarkerView
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.DropBoxManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentFarmBinding
import com.example.loofarm.databinding.FragmentHistoryValueBinding
import com.example.loofarm.model.Device
import com.example.loofarm.model.Farm
import com.example.loofarm.model.ManagerUser
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.collection.LLRBNode
import org.json.JSONArray
import org.json.JSONObject
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.TimeZone.getTimeZone

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryValueFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistoryValueFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHistoryValueBinding

    private val profitValues = ArrayList<Entry>()
    private val timeStamps = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // Inflate the layout for this fragment
        binding = FragmentHistoryValueBinding.inflate(layoutInflater, container, false)

        dataListing()

        initEvents()

        return binding.root
    }

    private fun initEvents() {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDateFormat(inputDate: String): String {
        // Định dạng của ngày giờ đầu vào
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        // Định dạng của ngày giờ đầu ra
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//        val outputFormatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")

        // Chuyển đổi ngày giờ
        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        val outputDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("GMT+7"))

        return outputDateTime.format(outputFormatter)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun dataListing() {
        timeStamps.clear() // Xóa danh sách trước khi thêm mới
        profitValues.clear() // Đảm bảo rằng profitValues được xóa trước khi thêm dữ liệu mới

        // Tạo một request queue
        val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())

        // Tạo một StringRequest
        val stringRequest = StringRequest(Request.Method.GET, ManagerUser.getLink() + "7",
            { response ->
                val jsonObject = JSONObject(response)
                val feedsArray = jsonObject.getJSONArray("feeds")

                for (i in 0..6) {
                    val feed = feedsArray.getJSONObject(i)
                    val value = feed.getString("field"+ManagerUser.getSensorCurrent().toString()).toFloatOrNull() ?: 0f
                    val time = feed.getString("created_at").toString()
                    profitValues.add(Entry(i.toFloat(), value))
                    val timeConv = convertDateFormat(time)
                    timeStamps.add(timeConv)


                }

                //error
                if (isAdded) {
                    setChart()
                }
            },
            { _ ->
                // Xử lý lỗi
            })
        requestQueue.add(stringRequest)
    }

    private fun setChart() {
        val dataSet: LineDataSet

        // Add data
        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
            dataSet = binding.chart.data.getDataSetByIndex(0) as LineDataSet
            dataSet.values = profitValues
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            dataSet = when (ManagerUser.getSensorCurrent()) {
                1 -> LineDataSet(profitValues, "Cảm biến nhiệt độ")
                2 -> LineDataSet(profitValues, "Cảm biến độ ẩm")
                3 -> LineDataSet(profitValues, "Cảm biến độ mặn")
                else -> LineDataSet(profitValues, "Cảm biến lưu lượng")
            }

            dataSet.setColors(Color.RED)
            dataSet.valueTextColor = Color.WHITE
            dataSet.circleColors = listOf(Color.BLUE)
            dataSet.lineWidth = 7f
            dataSet.circleRadius = 7f
            dataSet.setDrawValues(false)
            dataSet.setDrawFilled(true)
            dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.gradient)

            val data = LineData(dataSet)
            binding.chart.data = data
            binding.chart.invalidate() // Refreshing chart

            binding.chart.legend.isEnabled = false
            binding.chart.description.isEnabled = false

            // Thay đổi trục Y để chứa giá trị
            binding.chart.axisLeft.axisMinimum = 0f // Tùy chỉnh giá trị tối thiểu
            binding.chart.axisRight.isEnabled = false // Tắt trục Y bên phải

            // Thay đổi trục X để chứa thời gian
            val xAxis = binding.chart.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM // Giữ vị trí dưới
            xAxis.valueFormatter = object : ValueFormatter() {
                private val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                override fun getFormattedValue(value: Float): String {
                    return if (value.toInt() in timeStamps.indices) {
                        try {
                            val date = inputFormat.parse(timeStamps[value.toInt()])
                            date?.let { timeFormat.format(it) } ?: ""
                        } catch (e: Exception) {
                            ""
                        }
                    } else {
                        ""
                    }
                }
            }

            // Cài đặt thêm cho xAxis
            xAxis.granularity = 1f // Giữ khoảng cách giữa các nhãn
            xAxis.setDrawGridLines(false) // Không vẽ đường lưới cho trục X

            // Cài đặt trục Y
            val yAxis = binding.chart.axisLeft
            yAxis.axisMinimum = 0f // Đặt giá trị tối thiểu cho trục Y
            yAxis.axisMaximum = 100f // Đặt giá trị tối đa nếu cần thiết

            // Chuyển đổi định dạng thời gian
            val formattedTimeStamps = timeStamps.map { timeStamp ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = dateFormat.parse(timeStamp)
                val outputFormat = SimpleDateFormat("d/M HH:mm", Locale.getDefault())
                outputFormat.format(date)
            }

            // Thiết lập MarkerView với formattedTimeStamps
            val markerView = CustomMarkerView(requireContext(), R.layout.marker_view, formattedTimeStamps)
            binding.chart.marker = markerView // Gán MarkerView cho biểu đồ
        }
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryValueFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryValueFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}