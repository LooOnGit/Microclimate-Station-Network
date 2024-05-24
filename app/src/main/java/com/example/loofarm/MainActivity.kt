package com.example.loofarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loofarm.databinding.ActivityMainBinding
import com.example.loofarm.model.ManagerUser
import com.example.loofarm.ui.LoginFragment

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private val login = LoginFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initControls()
    }

    private fun initControls() {
        ManagerUser.createUser()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frLayout, login)
            commit()
        }
    }

}