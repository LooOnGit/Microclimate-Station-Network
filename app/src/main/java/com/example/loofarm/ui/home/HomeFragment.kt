package com.example.loofarm.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loofarm.R
import com.example.loofarm.adapter.FarmAdapter
import com.example.loofarm.adapter.OnItemClickListener
import com.example.loofarm.databinding.FragmentHomeBinding
import com.example.loofarm.model.ThingSpeakManager
import com.example.loofarm.ui.share_viewmodel.LooFarmViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(), OnItemClickListener {

    private var binding: FragmentHomeBinding? = null
    private val viewModel: LooFarmViewModel by viewModels()

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
        observeViewModels()
        initListeners()
    }

    override fun onItemClick(position: Int) {
        findNavController().navigate(R.id.action_homeFragment_to_farmFragment)
    }

    @SuppressLint("NewApi")
    private fun observeViewModels() {
        // Call data mới nhat
        viewModel.getThingSpeakData(
            channelId = ThingSpeakManager.channelId,
            apiKey = ThingSpeakManager.apiKey
        )

        viewModel.thingSpeakResponse.observe(viewLifecycleOwner) { thingSpeakResponse ->
            Log.d(TAG, "observeViewModels() called: $thingSpeakResponse")
            binding?.apply {
                rcFarmView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                rcFarmView.adapter = FarmAdapter(
                    thingSpeakResponse = thingSpeakResponse,
                    listener = this@HomeFragment
                )

                txtToday.text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            }
        }
    }

    private fun initListeners() {
        binding?.apply {
            btnUser.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_userInfoFragment)
            }

            btnNotify.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_notificationFragment)
            }
        }
    }



    companion object {
        private val TAG by lazy { HomeFragment::class.java.simpleName }
    }
}