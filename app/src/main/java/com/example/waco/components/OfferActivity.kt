package com.example.waco.components

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.waco.MainActivity
import com.example.waco.R
import com.example.waco.ui.fragments.*
import com.google.android.material.tabs.TabLayout

class OfferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer)

        // Toolbar setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = "Oferta"
            setDisplayHomeAsUpEnabled(true)
        }

        // Zmiana koloru tytułu i strzałki na szary
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.gray)) // upewnij się, że masz color "gray" w colors.xml
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.gray))

        // Inicjalizacja ViewPager i TabLayout
        val pagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.pager)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)

        // Sprawdzenie połączenia z internetem
        if (!isNetworkConnected()) {
            showNoInternetDialog()
        }
    }


    // Obsługa kliknięcia strzałki w toolbarze
    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        return true
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnected == true
    }

    private fun showNoInternetDialog() {
        AlertDialog.Builder(this)
            .setTitle("Brak połączenia z internetem")
            .setMessage("Proszę sprawdzić swoje połączenie internetowe.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    // Obsługa przycisku fizycznego cofania
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ -> super.onBackPressed() }
            .setNegativeButton("Nie") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    inner class SectionsPagerAdapter(fm: androidx.fragment.app.FragmentManager) :
        FragmentPagerAdapter(fm) {

        override fun getCount(): Int = 5

        override fun getItem(position: Int): Fragment = when (position) {
            0 -> PodkladyFragment()
            1 -> LakieryFragment()
            2 -> KatalizatoryFragment()
            3 -> RozpuszczalnikiFragment()
            4 -> DodatkiFragment()
            else -> throw IllegalStateException("Invalid tab position")
        }

        override fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> getString(R.string.p_tab2)
            1 -> getString(R.string.k_tab)
            2 -> getString(R.string.u_tab)
            3 -> getString(R.string.r_tab)
            4 -> getString(R.string.d_tab2)
            else -> null
        }
    }
}
