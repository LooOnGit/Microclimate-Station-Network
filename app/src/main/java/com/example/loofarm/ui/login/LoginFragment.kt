package com.example.loofarm.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class LoginFragment : Fragment() {
    private var binding: FragmentLoginBinding? = null
    private var auth: FirebaseAuth? = null

    private var devices: MutableList<Device> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding?.apply {
            btnLogin.setOnClickListener {
                ManagerUser.setLink("https://api.thingspeak.com/channels/${txtUserLogin.text.toString()}/feeds.json?api_key=${txtPassLogin.text.toString()}&results=")

                // Tạo một request queue
                val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())

                // Tạo một StringRequest
                val stringRequest = StringRequest(Request.Method.GET, ManagerUser.getLink() + "1",
                    { response ->
                        val jsonObject = JSONObject(response)
                        val feedsArray = jsonObject.getJSONArray("feeds")
                        val firstFeed = feedsArray.getJSONObject(0)

                        Log.d("TAG", "initListeners: ${txtUserLogin.text.toString()} // ${txtPassLogin.text.toString()}")
                        ManagerUser.setName("2606003")
                        ManagerUser.setPass("SHVQ54Q33Q9SP3RR")
                        devices.add(
                            Device(
                                "TEMP",
                                firstFeed.getString("field1").toFloatOrNull() ?: 0
                            )
                        )
                        devices.add(
                            Device(
                                "HUM",
                                firstFeed.getString("field2").toFloatOrNull() ?: 0
                            )
                        )
                        devices.add(
                            Device(
                                "SAL",
                                firstFeed.getString("field3").toFloatOrNull() ?: 0
                            )
                        )
                        devices.add(
                            Device(
                                "FLOW",
                                firstFeed.getString("field4").toFloatOrNull() ?: 0
                            )
                        )
                        ManagerUser.addFarm(
                            Farm(
                                ManagerUser.getName().toString(),
                                devices,
                                "23/07/2024"
                            )
                        )

                        Log.d("TAG", "initListeners: ${txtUserLogin.text.toString()} // ${txtPassLogin.text.toString()}")
//                        lifecycleScope.launch {
//                            delay(1000L)
//                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
//                        }
                    },
                    { _ ->
                        // Xử lý lỗi
                        Toast.makeText(requireContext(), "Tài khoản không đúng", Toast.LENGTH_SHORT)
                            .show()
                    })

                // Thêm request vào queue
                requestQueue.add(stringRequest)
            }
        }
    }
}
