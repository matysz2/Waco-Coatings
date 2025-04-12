package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.ui.fragments.DodatkiFragment
import com.example.waco.ui.fragments.KatalizatoryFragment
import com.example.waco.ui.fragments.KonwenteryFragment
import com.example.waco.ui.fragments.LakieryFragment
import com.example.waco.ui.fragments.PodkladyFragment
import com.example.waco.ui.fragments.RozpuszczalnikiFragment
import com.google.android.material.tabs.TabLayout

class OfferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        // Inicjalizacja Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Zmiana koloru tekstu na czarny
        toolbar.setTitleTextColor(resources.getColor(android.R.color.black))

        // Ustawienie tytułu i strzałki powrotu
        supportActionBar?.apply {
            title = "OFERTA"
            setDisplayHomeAsUpEnabled(true) // Umożliwia pokazanie strzałki


            // Zmieniamy strzałkę na czarną
        }

        // Inicjalizacja ViewPager i TabLayout
        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.pager)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        // Sprawdź połączenie z internetem
        if (!isNetworkConnected()) {
            // Jeśli brak połączenia z internetem, wyświetl AlertDialog
            showNoInternetDialog()
        }
    }

    // Funkcja sprawdzająca połączenie z internetem
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    // Wyświetlanie AlertDialog, jeśli brak połączenia
    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Brak połączenia z internetem")
            .setMessage("Proszę sprawdzić swoje połączenie internetowe.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false) // Zapobiega zamknięciu dialogu przez kliknięcie poza nim
            .show()
    }

    // Obsługuje naciśnięcie strzałki powrotu
    override fun onSupportNavigateUp(): Boolean {
        // Zwrócenie użytkownika do MainActivity po kliknięciu strzałki
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Zakończenie OfferActivity
        return true
    }

    // Adapter dla ViewPager
    inner class SectionsPagerAdapter(fm: androidx.fragment.app.FragmentManager) :
        FragmentPagerAdapter(fm) {

        // Określenie liczby tabów
        override fun getCount(): Int {
            return 5
        }

        // Zwracanie odpowiednich fragmentów dla tabów
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> PodkladyFragment()
                1 -> LakieryFragment()
                2 -> KatalizatoryFragment()
                3 -> RozpuszczalnikiFragment()
                4 -> DodatkiFragment()
                else -> throw IllegalStateException("Invalid tab position")
            }
        }

        // Tytuły tabów
        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.p_tab2)
                1 -> getString(R.string.k_tab)
                2 -> getString(R.string.u_tab)
                3 -> getString(R.string.r_tab)
                4 -> getString(R.string.d_tab2)
                else -> null
            }
        }
    }
}
