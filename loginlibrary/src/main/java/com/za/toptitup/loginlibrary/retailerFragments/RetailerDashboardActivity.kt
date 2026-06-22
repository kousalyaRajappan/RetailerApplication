package com.za.toptitup.loginlibrary.retailerFragments


import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.za.toptitup.loginlibrary.R
import com.za.toptitup.loginlibrary.databinding.ActivityRetailerDashboardBinding
import com.za.toptitup.loginlibrary.utils.Topitup

class RetailerDashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRetailerDashboardBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemNav()
        binding = ActivityRetailerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val license = intent.getStringExtra("LICENSE") ?: Topitup.TIU_LICENSE
        val posUserId = intent.getStringExtra("POS_USER_ID") ?: Topitup.POSUSER_ID
        val posName = intent.getStringExtra("POSUSER_NAME") ?: Topitup.POSUSER_NAME
        binding.tvAccount.text = posName
        binding.tvStoresCount.text = "#Stores in Group: 1"

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        binding.btnBack.setOnClickListener { navController.popBackStack() }
        binding.btnLogout.setOnClickListener { finish() }
        binding.btnViewBalance.setOnClickListener { /* show balance dialog */ }
        binding.btnSettings.setOnClickListener { /* show settings */ }
        binding.btnStoreInfo.setOnClickListener { /* show store info */ }

        setupBottomBar()

    }

    private fun setupBottomBar() {
        binding.root.findViewById<View>(R.id.item_call)?.setOnClickListener {
            /* handle call action */
        }
        binding.root.findViewById<View>(R.id.item_email)?.setOnClickListener {
            /* handle email action */
        }
        binding.root.findViewById<View>(R.id.item_web)?.setOnClickListener {
            /* handle web action */
        }
    }
    override fun onResume() {
        super.onResume()
        hideSystemNav()
    }
    private fun hideSystemNav() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )
    }
    fun showBackButton(show: Boolean, title: String = "") {
        binding.btnBack.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvModuleTitle.visibility = if (show) View.VISIBLE else View.GONE
        binding.glassUserCard?.visibility = if (show) View.GONE else View.VISIBLE
        binding.tvModuleTitle.text = title
    }
}