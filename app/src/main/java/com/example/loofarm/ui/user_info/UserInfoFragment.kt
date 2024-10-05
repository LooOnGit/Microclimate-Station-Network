package com.example.loofarm.ui.user_info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentUserInfoBinding
import com.example.loofarm.model.ManagerUser
import com.example.loofarm.model.SignInWithGoogle
import com.google.firebase.auth.FirebaseAuth

class UserInfoFragment : Fragment() {
    private var binding: FragmentUserInfoBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

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
       // binding?.txtUserName?.text = ManagerUser.getName().toString()
    }

    private fun initEvents() {
        binding?.apply {
            btnSignOut.setOnClickListener {
                auth.signOut()
                SignInWithGoogle(requireContext()).logOut()
                findNavController().navigate(R.id.action_userInfoFragment_to_loginFragment)
            }

            btnUpdateProfile.setOnClickListener {
                findNavController().navigate(R.id.action_userInfoFragment_to_updateInfoUserFragment)
            }
        }
    }
}
