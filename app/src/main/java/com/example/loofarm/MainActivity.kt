package com.example.loofarm

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.loofarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        createNavigationController()
    }

    private fun createNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as? NavHostFragment
        val navController = navHostFragment?.navController

        // Theo dõi navigation
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }

            Toast.makeText(this@MainActivity, "Navigated to $dest", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Navigated to $dest")
        }
    }

//    override fun onBackPressed() {
//        val navController = findNavController(R.id.navHostFragment)
//        if (!navController.popBackStack()) {
//            super.onBackPressed() // Nếu không còn Fragment nào, thực hiện hành động quay lại bình thường
//        }
//    }

    companion object {
        private val TAG by lazy { MainActivity::class.java.name }
    }
}
