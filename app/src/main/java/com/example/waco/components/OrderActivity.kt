package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.ui.fragments.AddProductFragment
import com.example.waco.ui.fragments.CurrentOrderFragment
import com.example.waco.ui.fragments.HistoryFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isNetworkAvailable()) {
            showNoInternetDialog()
            return
        }

        setContentView(R.layout.activity_order)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Zamówienia"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val userPrefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val adminPrefs = getSharedPreferences("admin_data", MODE_PRIVATE)

        val userId = userPrefs.getString("user_id", null) ?: adminPrefs.getString("user_id", null)
        val email = userPrefs.getString("email", null) ?: adminPrefs.getString("email", null)
        val firebaseToken = userPrefs.getString("firebase_token", null) ?: adminPrefs.getString("firebase_token", null)
        val isAdmin = adminPrefs.contains("user_id")

        Log.d("OrderActivity", "Dane w SharedPreferences:")
        Log.d("OrderActivity", "user_id: $userId")
        Log.d("OrderActivity", "email: $email")
        Log.d("OrderActivity", "firebase_token: $firebaseToken")

        if (userId == null || email == null || firebaseToken == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = OrderPagerAdapter(this, isAdmin)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                showLogoutConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz się wylogować?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ ->
                clearUserData()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Nie") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun clearUserData() {
        val userPrefs = getSharedPreferences("user_data", MODE_PRIVATE)
        val adminPrefs = getSharedPreferences("admin_data", MODE_PRIVATE)

        with(userPrefs.edit()) {
            clear()
            apply()
        }
        with(adminPrefs.edit()) {
            clear()
            apply()
        }

        Log.d("OrderActivity", "Wyczyszczono dane logowania.")
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ -> super.onBackPressed() }
            .setNegativeButton("Nie") { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    inner class OrderPagerAdapter(
        activity: FragmentActivity,
        private val isAdmin: Boolean
    ) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = if (isAdmin) 2 else 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AddProductFragment()
                1 -> CurrentOrderFragment()
                2 -> {
                    if (!isAdmin) HistoryFragment()
                    else throw IllegalStateException("Historia niedostępna dla admina")
                }
                else -> throw IllegalStateException("Nieprawidłowy numer zakładki")
            }
        }

        fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "Dodaj"
                1 -> "Aktualne"
                2 -> if (!isAdmin) "Historia" else ""
                else -> ""
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Brak połączenia")
            .setMessage("Brak połączenia z internetem. Sprawdź połączenie i spróbuj ponownie.")
            .setCancelable(false)
            .setPositiveButton("Zamknij") { _, _ -> finish() }
        builder.create().show()
    }
}
