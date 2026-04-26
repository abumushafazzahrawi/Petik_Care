package com.example.petikcare.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.petikcare.R
import com.example.petikcare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavController()
        setBottomNav()

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Matikan padding otomatis bawaan setupWithNavController
            v.setPadding(0, 0, 0, 0)

            // Atur posisi CardView (kartu pembungkus)
            val margin16dp = (16 * resources.displayMetrics.density).toInt()
            val params = binding.cardNavController.layoutParams as ConstraintLayout.LayoutParams
            params.bottomMargin = systemBars.bottom + margin16dp
            binding.cardNavController.layoutParams = params

            insets
        }

// Berikan padding top pada root agar status bar transparan tapi konten tidak tertutup
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }
    }

    private fun setNavController() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setBottomNav() {
        binding.bottomNav.setupWithNavController(navController)
    }
}