package com.example.waco


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.example.wacoapp.AboutWacoActivity

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
                R.id.nav_pod -> {}
                R.id.nav_kon -> {}
                R.id.nav_lak -> {}
                R.id.nav_kat -> {}
                R.id.nav_roz -> {}
                R.id.nav_polysk -> {}
                R.id.nav_polmat -> {}
                R.id.nav_mat -> {}
                R.id.nav_finish -> finish()
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // Obsługa kliknięcia na "O firmie"
    fun onAboutClick(view: View) {
        animateAndStart(view, AboutWacoActivity::class.java)
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Animacja przejścia
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }
}
