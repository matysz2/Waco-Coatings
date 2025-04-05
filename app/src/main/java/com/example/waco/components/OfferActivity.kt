package com.example.waco.components

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.waco.R
import com.example.waco.ui.fragments.DodatkiFragment
import com.example.waco.ui.fragments.KonwenteryFragment
import com.example.waco.ui.fragments.LakieryFragment
import com.example.waco.ui.fragments.PodkladyFragment
import com.example.waco.ui.fragments.RozpuszczalnikiFragment
import com.example.waco.ui.fragments.UtwardzaczFragment
import com.google.android.material.tabs.TabLayout

class OfferActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        // Inicjalizacja Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Ustawienie tytułu i strzałki powrotu
        supportActionBar?.apply {
            title = "OFERTA"
            setDisplayHomeAsUpEnabled(true)

            // Inicjalizacja ViewPager i TabLayout
            val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
            val viewPager: ViewPager = findViewById(R.id.pager)
            viewPager.adapter = pagerAdapter

            val tabLayout: TabLayout = findViewById(R.id.tabs)
            tabLayout.setupWithViewPager(viewPager)
        }


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
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
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


    // Adapter dla ViewPager
    inner class SectionsPagerAdapter(fm: androidx.fragment.app.FragmentManager) :
        FragmentPagerAdapter(fm) {

        // Określenie liczby tabów
        override fun getCount(): Int {
            return 6
        }

        // Zwracanie odpowiednich fragmentów dla tabów
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> PodkladyFragment()
                1 -> LakieryFragment()
                2 -> KonwenteryFragment() // Dodaj odpowiedni fragment
                3 -> UtwardzaczFragment()
                4 -> RozpuszczalnikiFragment() // Dodaj odpowiedni fragment
                5 -> DodatkiFragment() // Dodaj odpowiedni fragment
                else -> throw IllegalStateException("Invalid tab position")
            }
        }

        // Tytuły tabów (teraz 6 tytułów)
        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> getString(R.string.p_tab2)
                1 -> getString(R.string.l_tab)
                2 -> getString(R.string.k_tab)
                3 -> getString(R.string.u_tab)
                4 -> getString(R.string.r_tab)
                5 -> getString(R.string.d_tab2)
                else -> null
            }
        }
    }
}