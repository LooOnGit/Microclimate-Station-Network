package com.example.loofarm.ui.add_farm

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentAddFarmBinding

class AddFarmFragment : Fragment() {
    private var binding: FragmentAddFarmBinding? = null

    private val arrType = arrayOf("Rau", "Nấm")
    private lateinit var spninerAdapter: ArrayAdapter<String>

    var farmName: String = ""
    var sensor1: String = ""
    var sensor2: String = ""
    var actuator1: String = ""
    var actuator2: String = ""
    private var date: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddFarmBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initControls()
        initEvents()
    }

    private fun initEvents() {
        binding?.apply {
            spnFarmType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    farmName = arrType[position]
                    when (arrType[position]) {
                        arrType[0] -> {
                            sensor1 = "Nhiệt độ"
                            sensor2 = "Độ ẩm"
                            actuator1 = "Motor"
                            actuator2 = "Motor"
                            txtActuator2Title.text = "Motor 2:"
                            txtSensor2Title.text = "Cảm biến 2:"
                            imgPlant.setImageResource(R.drawable.img_vegetable)
                        }

                        arrType[1] -> {
                            sensor1 = "Nhiệt độ"
                            sensor2 = "Độ ẩm"
                            actuator1 = "Motor"
                            actuator2 = "Motor"
                            txtActuator2Title.text = "Motor 2:"
                            txtSensor2Title.text = "Cảm biến 2:"
                            imgPlant.setImageResource(R.drawable.img_mush)
                        }
                    }
                    txtSensor1.text = sensor1
                    txtSensor2.text = sensor2
                    txtActuator1.text = actuator1
                    txtActuator2.text = actuator2
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }

            btnSetPlantDay.setOnClickListener {
                DatePickerDialog(requireContext(), { _, i1, i2, i3 ->
                    date = "$i3/${i2 + 1}/$i1"
                    txtPlantDay.text = date
                }, 2023, 11, 7).show()
            }

            btnSaveFarm.setOnClickListener {
                findNavController().navigate(R.id.action_addFarmFragment_to_homeFragment)
            }

            btnExitFarm.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Question?")
                builder.setMessage("Are you sure you want to exit?")
                builder.apply {
                    setPositiveButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setNegativeButton("Yes") { _, _ ->
                        findNavController().navigate(R.id.action_addFarmFragment_to_homeFragment)
                    }
                }
                builder.create().show()
            }
            btnScanQR.setOnClickListener {
                findNavController().navigate(R.id.action_addFarmFragment_to_scanQRFragment)
            }
        }
    }

    private fun initControls() {
        spninerAdapter =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrType)
        spninerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
        binding?.spnFarmType?.adapter = spninerAdapter
    }
}
