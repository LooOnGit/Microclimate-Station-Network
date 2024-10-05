package com.example.loofarm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loofarm.databinding.ActivityMainBinding
import com.example.loofarm.model.ManagerUser
import com.example.loofarm.ui.LoginFragment

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val login = LoginFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
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
