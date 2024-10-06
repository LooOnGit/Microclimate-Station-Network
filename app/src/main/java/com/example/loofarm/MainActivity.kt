package com.example.loofarm

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.loofarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var navController: NavController? = null

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
        navController = navHostFragment?.navController

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

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when (navController?.currentDestination?.id) {
            R.id.homeFragment -> finish()
            else -> navController?.navigateUp()
        }
    }

    companion object {
        private val TAG by lazy { MainActivity::class.java.name }
    }
}
