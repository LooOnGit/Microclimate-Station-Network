package com.example.loofarm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentLoginBinding
import com.example.loofarm.model.Device
import com.example.loofarm.model.Farm
import com.example.loofarm.model.ManagerUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.json.JSONObject


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var databaseReference: DatabaseReference


    private var arrDevice:MutableList<Device> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
//        initControls()
        initEvents()
//        fetchDataFromThingSpeak()

        return binding.root
    }
    private fun initEvents() {
//        binding.btnLoginGG.setOnClickListener {
//            signInGoogle()
//        }
        binding.btnLogin.setOnClickListener {
            ManagerUser.setLink("https://api.thingspeak.com/channels/${binding.txtUserLogin.text.toString()}/feeds.json?api_key=${binding.txtPassLogin.text.toString()}&results=")

            // Tạo một request queue
            val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())

            // Tạo một StringRequest
            val stringRequest = StringRequest(Request.Method.GET, ManagerUser.getLink()+"1",
                { response ->
                    val jsonObject = JSONObject(response)
                    val feedsArray = jsonObject.getJSONArray("feeds")
                    val firstFeed = feedsArray.getJSONObject(0)

                    ManagerUser.setName(binding.txtUserLogin.text.toString())
                    ManagerUser.setPass(binding.txtPassLogin.text.toString())
                    arrDevice.add(Device("TEMP",firstFeed.getString("field1").toFloatOrNull() ?: 0))
                    arrDevice.add(Device("HUM",firstFeed.getString("field2").toFloatOrNull() ?: 0))
                    arrDevice.add(Device("SAL",firstFeed.getString("field3").toFloatOrNull() ?: 0))
                    arrDevice.add(Device("FLOW",firstFeed.getString("field4").toFloatOrNull() ?: 0))
                    ManagerUser.addFarm(Farm(ManagerUser.getName().toString(), arrDevice,"23/07/2024"))
//                            // Tạo Fragment mục tiêu
                    val targetFragment = HomeFragment()
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
//                        }
//                    }

                },
                { _ ->
                    // Xử lý lỗi
                    Toast.makeText(requireContext(), "Tài khoản không đúng", Toast.LENGTH_SHORT).show()
                })

            // Thêm request vào queue
            requestQueue.add(stringRequest)
        }
    }

}