package com.example.loofarm.ui

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentAddFarmBinding
import com.example.loofarm.model.Device
import com.example.loofarm.model.Farm
import com.example.loofarm.model.ManagerUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFarmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFarmFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAddFarmBinding

    private val arrType = arrayOf("Rau", "Nấm")
    private lateinit var spninerAdapter: ArrayAdapter<String>

    var farmName:String = ""
    var sensor1:String = ""
    var sensor2:String = ""
    var actuator1:String = ""
    var actuator2:String = ""
    private var date:String = ""
    var arrDevice:MutableList<Device> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddFarmBinding.inflate(layoutInflater, container, false)

        initControls()
        initEvents()

        return binding.root
    }

    private fun initEvents() {
        binding.spnFarmType.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                farmName = arrType[position]
                when(arrType[position]){
                    arrType[0]->{
                        sensor1 = "Temperature"
                        sensor2 = "Humidity"
                        actuator1 = "Watering"
                        actuator2 = "Fan"
                        binding.txtActuator2Title.text = "Actuator2"
                        binding.txtSensor2Title.text = "Sensor2"
                        binding.imgPlant.setImageResource(R.drawable.img_vegetable)
                    }
                    arrType[1]->{
                        sensor1 = "Temperature"
                        sensor2 = "Humidity"
                        actuator1 = "Watering"
                        actuator2 = "Refrigerator"
                        binding.txtActuator2Title.text = "Actuator2"
                        binding.txtSensor2Title.text = "Sensor2"
                        binding.imgPlant.setImageResource(R.drawable.img_mush)
                    }
                }
                binding.txtSensor1.text = sensor1
                binding.txtSensor2.text = sensor2
                binding.txtActuator1.text = actuator1
                binding.txtActuator2.text =actuator2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.btnSetPlantDay.setOnClickListener{
            DatePickerDialog(requireContext(), {_,i1,i2,i3->
                date = "$i3/${i2+1}/$i1"
                binding.txtPlantDay.text = date
            },2023,11,7).show()
        }

        binding.btnSaveFarm.setOnClickListener{
            if(sensor1!=""){
                arrDevice.add(Device(sensor1,0))
            }
            if(sensor2!=""){
                arrDevice.add(Device(sensor2,0))
            }
            if(actuator1!=""){
                arrDevice.add(Device(actuator1,0))
            }
            if(actuator2!=""){
                arrDevice.add(Device(actuator2,0))
            }

            ManagerUser.addFarm(Farm(farmName, arrDevice, date))
            val targetFragment = HomeFragment()
            // Thực hiện transaction để chuyển đổi Fragment
            val transaction = requireActivity().supportFragmentManager
                .beginTransaction().setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
            transaction.replace(R.id.frLayout, targetFragment)
            transaction.addToBackStack(null)
            transaction.commit()
            var databaseReference = Firebase.database.reference
            val users = ManagerUser.getUser()
            databaseReference.child(ManagerUser.getId().toString()).setValue(users)
        }

        binding.btnExitFarm.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Question?")
            builder.setMessage("Are you sure you want to exit?")
            builder.apply {
                setPositiveButton("No",
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    })
                setNegativeButton("Yes",
                    DialogInterface.OnClickListener { _, _ ->
                        val targetFragment = HomeFragment()
                        val transaction = requireActivity().supportFragmentManager
                            .beginTransaction().setCustomAnimations(
                                R.anim.slide_in,
                                R.anim.fade_out,
                                R.anim.fade_in,
                                R.anim.slide_out
                            )
                        transaction.replace(R.id.frLayout, targetFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    })
            }
            builder.create().show()
        }
    }

    private fun initControls() {
        spninerAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrType)
        spninerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
        binding.spnFarmType.adapter = spninerAdapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFarmFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFarmFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}