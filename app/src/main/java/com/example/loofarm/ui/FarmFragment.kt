package com.example.loofarm.ui

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentFarmBinding
import com.example.loofarm.databinding.FragmentMainInterfaceBinding
import com.example.loofarm.model.ManagerUser
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FarmFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentFarmBinding

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
        // Inflate the layout for this fragment
        binding = FragmentFarmBinding.inflate(layoutInflater, container, false)

        initControls()
        initEvents()

        return binding.root
    }

    private fun initEvents() {
        binding.lnTemp.setOnClickListener{
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

        binding.lnHum.setOnClickListener{
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

        binding.lnSal.setOnClickListener{
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

        binding.lnFlow.setOnClickListener{
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

//        binding.btnScanQRNode.setOnClickListener{
//            val targetFragment = ScanQRFragment()
//            // Thực hiện transaction để chuyển đổi Fragment
//            val transaction = requireActivity().supportFragmentManager
//                .beginTransaction().setCustomAnimations(
//                    R.anim.slide_in,
//                    R.anim.fade_out,
//                    R.anim.fade_in,
//                    R.anim.slide_out
//                )
//            transaction.replace(R.id.frLayout, targetFragment)
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initControls() {
//        Toast.makeText(requireContext(), "${ManagerUser.getPosition()}", Toast.LENGTH_SHORT).show()
        binding.txtStartDate.text = ManagerUser.getDate(ManagerUser.getPosition())
        binding.txtNameSensor1.text = "Nhiệt độ"
        binding.txtValueSensor1.text = ManagerUser.getDeviceValue(ManagerUser.getPosition(), 0).toString() + " °C"
        binding.txtNameSensor2.text = "Độ Ẩm"
        binding.txtValueSensor2.text = ManagerUser.getDeviceValue(ManagerUser.getPosition(), 1).toString() + " %"
        binding.txtNameSensor3.text = "Độ mặn"
        binding.txtValueSensor3.text = String.format("%.2f", ManagerUser.getDeviceValue(ManagerUser.getPosition()?: 0, 2)) + " ppt"
        binding.txtNameSensor4.text = "Lưu lượng"
        binding.txtValueSensor4.text = String.format("%.4f", ManagerUser.getDeviceValue(ManagerUser.getPosition()?: 0, 3)) + " L"

        var dateStart = ManagerUser.getDate(ManagerUser.getPosition())
        val parts = dateStart?.split("/")

        val resultList = parts?.map { it.trim() }
        var date:MutableList<Int> = mutableListOf()
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

        binding.txtHavestDate.text = formattedNewDate
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FarmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}