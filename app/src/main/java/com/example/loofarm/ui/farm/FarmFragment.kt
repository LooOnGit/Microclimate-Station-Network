package com.example.loofarm.ui.farm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentFarmBinding
import com.example.loofarm.model.ManagerUser
import com.example.loofarm.ui.BlankFragment2
import com.example.loofarm.ui.history_value.HistoryValueFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FarmFragment : Fragment() {
    private var binding: FragmentFarmBinding? = null

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
            lnTemp.setOnClickListener {
                ManagerUser.setSensorCurrent(1)
                val targetFragment = HistoryValueFragment()
                // Thực hiện transaction để chuyển đổi Fragment
                val transaction = requireActivity().supportFragmentManager
                    .beginTransaction().setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                //transaction.replace(R.id.frLayout, targetFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            lnHum.setOnClickListener {
                ManagerUser.setSensorCurrent(2)
                val targetFragment = HistoryValueFragment()
                // Thực hiện transaction để chuyển đổi Fragment
                val transaction = requireActivity().supportFragmentManager
                    .beginTransaction().setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                //transaction.replace(R.id.frLayout, targetFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            lnSal.setOnClickListener {
                ManagerUser.setSensorCurrent(3)
                val targetFragment = HistoryValueFragment()
                // Thực hiện transaction để chuyển đổi Fragment
                val transaction = requireActivity().supportFragmentManager
                    .beginTransaction().setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                //transaction.replace(R.id.frLayout, targetFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            lnFlow.setOnClickListener {
                ManagerUser.setSensorCurrent(4)
//            val targetFragment = HistoryValueFragment()
                val targetFragment = BlankFragment2()
                // Thực hiện transaction để chuyển đổi Fragment
                val transaction = requireActivity().supportFragmentManager
                    .beginTransaction().setCustomAnimations(
                        R.anim.slide_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.slide_out
                    )
                //transaction.replace(R.id.frLayout, targetFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initControls() {
        binding?.apply {
            txtStartDate.text = ManagerUser.getDate(ManagerUser.getPosition())
            txtNameSensor1.text = "Nhiệt độ"
            txtValueSensor1.text =
                ManagerUser.getDeviceValue(ManagerUser.getPosition(), 0).toString() + " °C"
            txtNameSensor2.text = "Độ Ẩm"
            txtValueSensor2.text =
                ManagerUser.getDeviceValue(ManagerUser.getPosition(), 1).toString() + " %"
            txtNameSensor3.text = "Độ mặn"
            txtValueSensor3.text = String.format(
                "%.2f",
                ManagerUser.getDeviceValue(ManagerUser.getPosition() ?: 0, 2)
            ) + " ppt"
            txtNameSensor4.text = "Lưu lượng"
            txtValueSensor4.text = String.format(
                "%.4f",
                ManagerUser.getDeviceValue(ManagerUser.getPosition() ?: 0, 3)
            ) + " L"
        }

        var dateStart = ManagerUser.getDate(ManagerUser.getPosition())
        val parts = dateStart?.split("/")

        val resultList = parts?.map { it.trim() }
        var date: MutableList<Int> = mutableListOf()
        if (resultList != null) {
            for (element in resultList) {
                date.add(element.toInt())
            }
        }

        val originalDate = LocalDate.of(date[2], date[1], date[0])
        val daysToAdd = 90

        val newDate = originalDate.plusDays(daysToAdd.toLong())

        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val formattedNewDate = newDate.format(formatter)

        binding?.txtHavestDate?.text = formattedNewDate
    }
}
