package com.example.loofarm.ui.farm

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentFarmBinding
import com.example.loofarm.model.ThingSpeakManager
import com.example.loofarm.ui.share_viewmodel.LooFarmViewModel
import com.example.loofarm.utils.addDays
import com.example.loofarm.utils.formatDate

class FarmFragment : Fragment() {
    private var binding: FragmentFarmBinding? = null
    private val viewModel: LooFarmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFarmBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControls()
        initEvents()
    }

    private fun initEvents() {
        binding?.apply {
            llTemp.setOnClickListener {
                navigateToHistory(sensorId = ThingSpeakManager.SENSOR_TEMP)
            }

            llHum.setOnClickListener {
                navigateToHistory(sensorId = ThingSpeakManager.SENSOR_HUM)
            }

            llSal.setOnClickListener {
                navigateToHistory(sensorId = ThingSpeakManager.SENSOR_SAL)
            }

            llFlow.setOnClickListener {
                navigateToHistory(sensorId = ThingSpeakManager.SENSOR_FLOW)
            }
        }
    }

    private fun navigateToHistory(sensorId: Int) {
        val bundle = Bundle().apply {
            putInt(ThingSpeakManager.SENSOR_KEY, sensorId)
        }
        findNavController().navigate(R.id.action_farmFragment_to_historyFragment, bundle)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun initControls() {
        viewModel.getThingSpeakData(
            channelId = ThingSpeakManager.channelId,
            apiKey = ThingSpeakManager.apiKey
        )
        viewModel.thingSpeakResponse.observe(viewLifecycleOwner) { thingSpeakResponse ->
            binding?.apply {
                txtStartDate.text = thingSpeakResponse?.channel?.createdAt.toString().formatDate()
                txtHavestDate.text =
                    thingSpeakResponse?.channel?.createdAt.toString().addDays(90L).formatDate()

                // Temp sensor
                val temp = thingSpeakResponse?.feeds?.firstOrNull()?.field1
                tvSensorTempValue.text = "$temp °C"
                // Hum sensor
                val hum = thingSpeakResponse?.feeds?.firstOrNull()?.field2
                tvSensorHumValue.text = "$hum %"
                // sal sensor
                val sal = thingSpeakResponse?.feeds?.firstOrNull()?.field3
                tvSensorSalValue.text = "$sal ppt"
                // flow sensor
                val flow = thingSpeakResponse?.feeds?.firstOrNull()?.field4
                tvSensorFlowValue.text = "$flow L"
            }
        }
    }
}
