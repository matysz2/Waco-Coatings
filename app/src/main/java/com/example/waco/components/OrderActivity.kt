package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.waco.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.waco.MainActivity
import com.example.waco.adapter.OrderHistoryAdapter
import com.example.waco.ui.fragments.AddProductFragment
import com.example.waco.ui.fragments.CurrentOrderFragment
import com.example.waco.ui.fragments.OrderHistoryFragment

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sprawdzenie połączenia z internetem
        if (!isNetworkAvailable()) {
            showNoInternetDialog()
            return
        }

        setContentView(R.layout.activity_order)

        // Inicjalizacja Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Zamówienia"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Sprawdzanie logowania użytkownika
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        // Ustawienie TabLayout i ViewPager2
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = OrderPagerAdapter(this)
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
        when (item.itemId) {
            R.id.logout -> {
                showLogoutConfirmationDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wylogować się?")
            .setCancelable(false)
            .setPositiveButton("Tak") { dialog, id ->
                clearUserData()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Nie") { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun clearUserData() {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { dialog, id ->
                super.onBackPressed()
            }
            .setNegativeButton("Nie") { dialog, id ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    // Adapter do ViewPager2
    inner class OrderPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AddProductFragment()
                1 -> CurrentOrderFragment()
                2 -> OrderHistoryFragment()
                else -> throw IllegalStateException("Invalid tab position")
            }
        }

        fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Dodaj"
                1 -> "Aktualne"
                2 -> "Historia"
                else -> null
            }
        }
    }

    // Sprawdzenie dostępności internetu
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    // Wyświetlenie komunikatu o braku internetu
    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Brak połączenia")
            .setMessage("Brak połączenia z internetem. Sprawdź połączenie i spróbuj ponownie.")
            .setCancelable(false)
            .setPositiveButton("Zamknij") { _, _ ->
                finish()
            }
        builder.create().show()
    }
}
