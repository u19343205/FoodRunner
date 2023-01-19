package com.codewithharsh.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.fragment.*
import com.codewithharsh.foodrunner.util.Preferences
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frame: FrameLayout
    lateinit var navigationView: NavigationView


    var previousMenuItem: MenuItem? = null

    private lateinit var preferences: Preferences
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = Preferences(this@MainActivity)
        sharedPreferences =
            this@MainActivity.getSharedPreferences(preferences.PREF_NAME, preferences.PRIVATE_MODE)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        setupToolbar()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, MyProfileFragment())
                        .commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()

                }
                R.id.favouriteRestaurant -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouriteRestaurantsFragment())
                        .commit()
                    supportActionBar?.title = "Favourite Restaurant's"
                    drawerLayout.closeDrawers()
                }

                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, OrderHistoryFragment())
                        .commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FAQsFragment())
                        .commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()

                }
                R.id.logOut -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want ot log out?")
                    dialog.setPositiveButton("YES") { _, _ ->
                        preferences.setLogIn(false)
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        Volley.newRequestQueue(this).cancelAll(this::class.java.simpleName)
                        ActivityCompat.finishAffinity(this@MainActivity)

                    }
                    dialog.setNegativeButton("NO") { _, _ ->
                        openHome()
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

        val headerView =
            LayoutInflater.from(this@MainActivity).inflate(R.layout.drawer_header, null)
        val tvUserName: TextView = headerView.findViewById(R.id.tvUserName)

        tvUserName.text = sharedPreferences.getString("user_name", null)
        val tvMobileNumber: TextView = headerView.findViewById(R.id.tvMobileNumber)
        val imgUser: ImageView = headerView.findViewById(R.id.imgUserImgae)

        val phone = "+91-${sharedPreferences.getString("user_mobile_number", null)}"
        tvMobileNumber.text = phone
        navigationView.addHeaderView(headerView)

// if the user clicks on the name of the user we transit to my profile fragment
        tvUserName.setOnClickListener {
            val myProfile = MyProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, myProfile)
            transaction.commit()

            supportActionBar?.title = "My Profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 100)
            navigationView.setCheckedItem(R.id.myProfile)
        }

//same with the case of the img
        imgUser.setOnClickListener {
            val myProfile = MyProfileFragment()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, myProfile)

            supportActionBar?.title = "My Profile"
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 100)
            navigationView.setCheckedItem(R.id.myProfile)
        }


    }

    fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.frame, fragment).commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {

        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }
}


//    private val onBackInvokedCallback = object : OnBackPressedCallback(true){
//        override fun handleOnBackPressed() {
//    val fragment = supportFragmentManager.findFragmentById(R.id.frame)
//
//            when (fragment){
//                !is HomeFragment -> openHome()
//                else -> super.onBackPressed()
//            }
//        }
//    }