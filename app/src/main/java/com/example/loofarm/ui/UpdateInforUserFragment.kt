package com.example.loofarm.ui

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentUpdateInforUserBinding
import com.example.loofarm.model.ManagerUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class UpdateInforUserFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentUpdateInforUserBinding
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
        binding = FragmentUpdateInforUserBinding.inflate(layoutInflater, container, false)

        initEvents()

        return binding.root
    }

    private fun initEvents() {
        binding.btnSaveInfor.setOnClickListener{
            ManagerUser.setName(binding.edtUserName.text.toString())
            val targetFragment = UserInforFragment()
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

        binding.btnExitInfor.setOnClickListener{
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
                        val targetFragment = UserInforFragment()
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateInforUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}