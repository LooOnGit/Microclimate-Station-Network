package com.example.loofarm.ui.update_user_info

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentUpdateUserInfoBinding

class UpdateInfoUserFragment : Fragment() {
    private var binding: FragmentUpdateUserInfoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateUserInfoBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvents()
    }

    private fun initEvents() {
        binding?.apply {
            btnSaveInfor.setOnClickListener {
                findNavController().navigate(R.id.action_updateInfoUserFragment_to_userInfoFragment)
            }

            btnExitInfor.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Question?")
                builder.setMessage("Are you sure you want to exit?")
                builder.apply {
                    setPositiveButton("No",
                        DialogInterface.OnClickListener { dialog, _ ->
                            dialog.dismiss()
                        })
                    setNegativeButton("Yes") { _, _ ->
                        findNavController().navigate(R.id.action_updateInfoUserFragment_to_userInfoFragment)
                    }
                }
                builder.create().show()
            }
        }
    }
}
