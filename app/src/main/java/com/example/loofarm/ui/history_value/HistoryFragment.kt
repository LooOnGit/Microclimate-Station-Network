package com.example.loofarm.ui.history_value

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentHistoryBinding
import com.example.loofarm.model.ThingSpeakManager
import com.example.loofarm.utils.MyXAxisValueFormatter
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class HistoryFragment : Fragment() {
    private var binding: FragmentHistoryBinding? = null

    private var sensor: Int? = null
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sensor = it.getInt(ThingSpeakManager.SENSOR_KEY) // Lấy dữ liệu từ Bundle
            Log.d(TAG, "onCreate() called: sensor = $sensor")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModels()
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
//    }

    private fun observeViewModels() {
        viewModel.getFeeds()
        // Observe feeds LiveData
        viewModel.feeds.observe(viewLifecycleOwner) { feeds ->
            Log.d(
                TAG,
                "observeViewModels() called with: feeds = $feeds"
            )
            val listTime = feeds?.map { it.createdAt }
            val dataSensors = when (sensor) {
                ThingSpeakManager.SENSOR_TEMP -> feeds?.map {
                    it.field1.toString().toFloatOrNull() ?: 0f
                }

                ThingSpeakManager.SENSOR_HUM -> feeds?.map {
                    it.field2.toString().toFloatOrNull() ?: 0f
                }

                ThingSpeakManager.SENSOR_SAL -> feeds?.map {
                    it.field3.toString().toFloatOrNull() ?: 0f
                }

                ThingSpeakManager.SENSOR_FLOW -> feeds?.map {
                    it.field4.toString().toFloatOrNull() ?: 0f
                }

                else -> List(feeds?.size ?: 0) { 0f }
            }
            drawChart(listTime = listTime, dataSensors = dataSensors)
        }

        // Observe loading LiveData
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            // Show or hide progress bar
            Log.d(TAG, "observeViewModels() called with: isLoading = $isLoading")
            binding?.progressBar?.isVisible = isLoading
        }

        // Observe error LiveData
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Log.d(
                TAG,
                "observeViewModels() called with: errorMessage = $errorMessage"
            )
            if (errorMessage != null) {
                // Show error message
                Toast.makeText(requireContext(), "Get data fail", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun drawChart(listTime: List<String?>?, dataSensors: List<Float>?) {
        Log.d(TAG, "drawChart() called with: listTime = $listTime, dataSensors = $dataSensors")
        val entries = mutableListOf<Entry>()

        dataSensors?.forEachIndexed { index, value -> entries.add(Entry(index.toFloat(), value)) }

        val lineDataSet = LineDataSet(
            entries,
            when (sensor) {
                ThingSpeakManager.SENSOR_TEMP -> "Temp sensor"

                ThingSpeakManager.SENSOR_HUM -> "Hum sensor"

                ThingSpeakManager.SENSOR_SAL -> "SAL sensor"

                ThingSpeakManager.SENSOR_FLOW -> "Flow sensor"

                else -> "Unknown"
            }
        ) // Set label for the line

        val color = ContextCompat.getColor(
            requireContext(),
            when (sensor) {
                ThingSpeakManager.SENSOR_TEMP -> R.color.temp_color

                ThingSpeakManager.SENSOR_HUM -> R.color.hum_color

                ThingSpeakManager.SENSOR_SAL -> R.color.sal_color

                ThingSpeakManager.SENSOR_FLOW -> R.color.flow_color

                else -> android.R.color.holo_blue_light
            }
        )
        lineDataSet.color = color // Line color

        lineDataSet.valueTextColor =
            ContextCompat.getColor(requireContext(), android.R.color.black) // Value text color

        // Optional: Customize line appearance
        lineDataSet.lineWidth = 2f
        lineDataSet.circleRadius = 5f
        lineDataSet.setCircleColor(color)

        // Set data to the chart
        binding?.lineChart?.apply {
            data = LineData(lineDataSet)

            // Customize chart appearance
            xAxis?.apply {
                labelRotationAngle = -45f // Rotate x-axis labels
                valueFormatter = MyXAxisValueFormatter(listTime) // Set custom x-axis labels
                setLabelCount(listTime?.size ?: 0, true) // Hiển thị tất cả các nhãn
                granularity = 1f // Đặt khoảng cách tối thiểu giữa các nhãn
                isGranularityEnabled = true
            }

            // Optional: Customize description
            val desc = Description()
            desc.text = "Per-minute data"
            description = desc

            invalidate() // Refresh the chart
        }
    }

    companion object {
        private val TAG by lazy { HistoryFragment::class.java.simpleName }
    }
}
