package com.example.loofarm.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentCompanyInforBinding
import com.example.loofarm.databinding.FragmentLoginBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CompanyInforFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CompanyInforFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentCompanyInforBinding
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
        binding = FragmentCompanyInforBinding.inflate(layoutInflater, container, false)

        initEvents()

        return binding.root
    }

    private fun initEvents() {
        binding.btnOutCompanyInfor.setOnClickListener{
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
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompanyInforFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CompanyInforFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}