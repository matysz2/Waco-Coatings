package com.example.waco.components

import android.content.Intent
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
        setContentView(R.layout.activity_order)

        // Inicjalizacja Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Zamówienia"  // Tytuł na pasku narzędzi
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Przycisk powrotu

        // Sprawdzanie logowania użytkownika
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId == null) {
            // Jeśli użytkownik nie jest zalogowany, przekieruj do ekranu logowania
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Zakończenie bieżącej aktywności
            return
        }

        // Ustawienie TabLayout i ViewPager2
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = OrderPagerAdapter(this)
        viewPager.adapter = adapter

        // Połączenie TabLayout z ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getPageTitle(position)
        }.attach()
    }

    // Ładowanie menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_logout, menu)  // Załaduj plik menu_logout.xml
        return super.onCreateOptionsMenu(menu)
    }

    // Obsługa kliknięcia w przycisk wylogowania
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
                // Wyczyść dane użytkownika z pamięci
                clearUserData()
                // Przejdź do MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()  // Zakończ obecną aktywność
            }
            .setNegativeButton("Nie") { dialog, id ->
                dialog.dismiss()  // Anulowanie
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

    // Obsługa kliknięcia strzałki w toolbarze (powrót do MainActivity)
    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)  // Uruchamiamy MainActivity
        finish()  // Zakończ obecna aktywność, aby nie pozostała w stosie
        return true
    }

    // Adapter dla ViewPager2
    inner class OrderPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

        override fun getItemCount(): Int {
            return 3 // Liczba tabów
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AddProductFragment() // Podstawowy fragment dla pierwszego tab-a
                1 -> CurrentOrderFragment() // Drugi fragment
                2 -> OrderHistoryFragment() // Trzeci fragment
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

    override fun onBackPressed() {
        // Tworzenie okna dialogowego potwierdzenia
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { dialog, id ->
                // Wywołanie standardowej akcji wyjścia (kończy aktywność)
                super.onBackPressed()
            }
            .setNegativeButton("Nie") { dialog, id ->
                dialog.dismiss()  // Anulowanie zamknięcia aplikacji
            }

        // Wyświetlenie okna dialogowego
        val alert = builder.create()
        alert.show()
    }
}
