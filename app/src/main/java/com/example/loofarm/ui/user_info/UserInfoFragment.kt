package com.example.loofarm.ui.user_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentUserInfoBinding
import com.example.loofarm.ui.share_viewmodel.LooFarmViewModel

class UserInfoFragment : Fragment() {
    private var binding: FragmentUserInfoBinding? = null
    private val viewModel: LooFarmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserInfoBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvents()
        initControls()
    }

    private fun initControls() {
        viewModel.thingSpeakResponse.observe(viewLifecycleOwner) {
            // TODO: API update add username
            binding?.txtUserName?.text = it?.channel?.name
        }
    }

    private fun initEvents() {
        binding?.apply {
            btnSignOut.setOnClickListener {
                findNavController().navigate(R.id.action_userInfoFragment_to_loginFragment)
            }

            btnUpdateProfile.setOnClickListener {
                findNavController().navigate(R.id.action_userInfoFragment_to_updateInfoUserFragment)
            }
        }
    }
}
