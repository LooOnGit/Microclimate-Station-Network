package com.example.loofarm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loofarm.adapter.FarmAdapter
import com.example.loofarm.R
import com.example.loofarm.databinding.FragmentMainInterfaceBinding
import com.example.loofarm.model.ManagerUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentMainInterfaceBinding
    private lateinit var itemAdapter: FarmAdapter
    private lateinit var auth : FirebaseAuth
    private var listFarms:MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

//        Toast.makeText(context, "${ManagerUser.getId()}", Toast.LENGTH_LONG).show()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainInterfaceBinding.inflate(layoutInflater, container, false)
        initViews()
        initEvents()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun initViews() {
        listFarms.clear()
        val numberFarm = ManagerUser.getFarmsSize()
        if (numberFarm != 0){
            if (numberFarm != null) {
                for (i in 0..(numberFarm-1)!!) {
                    listFarms.add(ManagerUser.getFarmName(i))
                }
            }
        }
        binding.rcFarmView.layoutManager = GridLayoutManager(requireActivity(), 2)
        itemAdapter = FarmAdapter(listFarms)
        binding.rcFarmView.adapter = itemAdapter
    }

    private fun initEvents() {
//        var databaseReference = Firebase.database.reference
//        val users = ManagerUser.getUser()
//        databaseReference.child(ManagerUser.getId().toString()).setValue(users)
//        Toast.makeText(requireActivity(), "thad", Toast.LENGTH_SHORT).show()

        itemAdapter.onItemClick = { position ->
            ManagerUser.setPosition(position)
            val targetFragment = FarmFragment()
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

        binding.btnUser.setOnClickListener{
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
        }

        binding.btnCompanyInfor.setOnClickListener{
            val targetFragment = CompanyInforFragment()
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

        binding.btnAddFarm.setOnClickListener{
            val targetFragment = AddFarmFragment()
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
}