package com.example.waco


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.waco.components.AboutWacoActivity
import com.example.waco.components.ColorListActivity
import com.example.waco.components.ColorNcsCamera
import com.example.waco.components.ColorRGBListActivity
import com.example.waco.components.ColorRalCamera
import com.example.waco.components.ContactActivity
import com.example.waco.components.OfferActivity
import com.example.waco.components.LoginActivity
import com.example.waco.components.OrderActivity


class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)

        val titleTextView: TextView = findViewById(R.id.toolbar_title)
        titleTextView.text = "WACO COATINGS"

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.check_colors -> { goToCheckColors() }
                R.id.check_ncs -> {goToCheckNcs()}
                R.id.check_ral_camera -> {goToCheckRalCamera()}
                R.id.check_ncs_camera -> {goToCheckNcsCamera()}

                R.id.nav_finish -> {
                    showExitDialog()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // Obsługa kliknięcia na "O firmie"
    fun onAboutClick(view: View) {
        animateAndStart(view, AboutWacoActivity::class.java)
    }

    // Obsługa kliknięcia na "Oferta"
    fun onOfferClick(view: View) {
        animateAndStart(view, OfferActivity::class.java)
    }

    fun onOrderClick(view: View) {
        animateAndStart(view, OrderActivity::class.java)
    }

    fun onLoginClick(view: View) {
        animateAndStart(view, LoginActivity::class.java)
    }
    fun onContactClick(view: View) {
        animateAndStart(view, ContactActivity::class.java)
    }


    // Menu boczne akcje

    private fun goToCheckColors() {
        val intent = Intent(this, ColorListActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun goToCheckNcs() {
        val intent = Intent(this, ColorRGBListActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun goToCheckNcsCamera() {
        val intent = Intent(this, ColorNcsCamera::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun goToCheckRalCamera() {
        val intent = Intent(this, ColorRalCamera::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // Animacja przejścia do nowej aktywności
    fun animateAndStart(view: View, destination: Class<*>) {
        val anim = AnimationUtils.loadAnimation(this, R.anim.scale_click)
        view.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@MainActivity, destination)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    // Pokazanie dialogu wyjścia
    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ ->
                finish() // Kończy aplikację
            }
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss() // Zamknie dialog
            }
        val alert = builder.create()
        alert.show()
    }

    // Nadpisanie przycisku "Wstecz" telefonu
    fun onExitClick(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy na pewno chcesz wyjść z aplikacji?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ ->
                super.onBackPressed() // Exit the activity
            }
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog, do nothing
            }
        val alert = builder.create()
        alert.show()
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
