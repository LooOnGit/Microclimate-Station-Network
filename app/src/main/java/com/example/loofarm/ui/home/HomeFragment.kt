package com.example.loofarm.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loofarm.R
import com.example.loofarm.adapter.FarmAdapter
import com.example.loofarm.databinding.FragmentHomeBinding
import com.example.loofarm.model.ManagerUser
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var itemAdapter: FarmAdapter
    private lateinit var auth: FirebaseAuth
    private var listFarms: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initEvents()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        listFarms.clear()
        val numberFarm = ManagerUser.getFarmsSize()
        if (numberFarm != 0) {
            if (numberFarm != null) {
                for (i in 0..(numberFarm - 1)!!) {
                    listFarms.add(ManagerUser.getFarmName(i))
                }
            }
        }
        binding?.apply {
            rcFarmView.layoutManager = GridLayoutManager(requireActivity(), 1)
            itemAdapter = FarmAdapter(listFarms)
            rcFarmView.adapter = itemAdapter
            txtToday.text = getCurrentDate()
        }
    }

    private fun initEvents() {
        itemAdapter.onItemClick = { position ->
            ManagerUser.setPosition(position)
            findNavController().navigate(R.id.action_homeFragment_to_farmFragment)
        }

        binding?.apply {
            btnUser.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_userInfoFragment)
            }

            btnNotify.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Định dạng ngày
        return currentDate.format(formatter)
    }
}