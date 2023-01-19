package com.codewithharsh.foodrunner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.adapter.RestaurantMenuAdapter
import com.codewithharsh.foodrunner.database.CartDatabase
import com.codewithharsh.foodrunner.database.CartEntity
import com.codewithharsh.foodrunner.model.MenuItems
import com.codewithharsh.foodrunner.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {

    private lateinit var resMenuToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerRestaurantMenu: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var menuProgressLayout: RelativeLayout
    private lateinit var menuProgressBar: ProgressBar

    lateinit var btnProceedToCart: Button
    lateinit var recyclerAdapter: RestaurantMenuAdapter

    val menuInfoList = arrayListOf<MenuItems>()
    val orderFoodList = arrayListOf<MenuItems>()

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var goToCart: Button
        var resId: Int = 0
        var resName: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        resMenuToolbar = findViewById(R.id.resMenuToolbar)
        recyclerRestaurantMenu = findViewById(R.id.recyclerRestaurantMenu)
        layoutManager = LinearLayoutManager(this@RestaurantMenuActivity)
        menuProgressLayout = findViewById(R.id.menuProgressLayout)
        menuProgressLayout.visibility = View.VISIBLE
        menuProgressBar = findViewById(R.id.menuProgressBar)

        btnProceedToCart = findViewById(R.id.btnGoToCart)
        btnProceedToCart.visibility = View.GONE

        // here we get  back the values we passed earlier
        if (intent != null) {
            resId = intent.getIntExtra("id", 0)
            resName = intent.getStringExtra("name").toString()
        }
        if (resId == 0) {
            Toast.makeText(this@RestaurantMenuActivity, "same error", Toast.LENGTH_SHORT).show()
        }
        setUpToolbar(resName)

        setUpRestaurantMenu()

        btnProceedToCart.setOnClickListener {
            proceedToCart(resId)
        }
    }

    fun proceedToCart(resId: Int?) {

        // gson is used to convert the array data into simpler strings which will be useful to store in database
        val gson = Gson()
        val menuItems = gson.toJson(orderFoodList)// data in this array is converted to string
        val async = ItemsInCart(this@RestaurantMenuActivity, resId.toString(), menuItems, 1).execute()
        val result = async.get()

        if (result) {
            val data = Bundle()
            data.putInt("resId", resId as Int)
            data.putString("resName", resName)
            val intent = Intent(this@RestaurantMenuActivity, CartActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        } else {
            Toast.makeText(
                this@RestaurantMenuActivity,
                "Some  unexpected error occurred",
                Toast.LENGTH_SHORT
            ).show()

        }

    }


    fun setUpRestaurantMenu() {
        if (ConnectionManager().checkConnectivity(this@RestaurantMenuActivity)) {
            val queue = Volley.newRequestQueue(this@RestaurantMenuActivity)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url + resId, null,
                    Response.Listener {
                        println("Response is $it")

                        try {
                            val menuObject = it.getJSONObject("data")
                            val success = menuObject.getBoolean("success")
                            if (success) {
                                menuProgressLayout.visibility = View.GONE
                                val data = menuObject.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val menuJsonObject = data.getJSONObject(i)
                                    val menuInfo = MenuItems(
                                        menuJsonObject.getString("id"),
                                        menuJsonObject.getString("name"),
                                        menuJsonObject.getString("cost_for_one")
                                    )
                                    menuInfoList.add(menuInfo)
                                    recyclerAdapter =
                                        RestaurantMenuAdapter(this@RestaurantMenuActivity,
                                            menuInfoList,
                                            object : RestaurantMenuAdapter.OnItemClickListener {
                                                override fun onAddItemClick(menuItem: MenuItems) {
                                                    orderFoodList.add(menuItem)
                                                    if (orderFoodList.size > 0) {
                                                        btnProceedToCart.visibility = View.VISIBLE
                                                        RestaurantMenuAdapter.cartEmpty = false
                                                    }
                                                }

                                                override fun onRemoveItemClick(menuItem: MenuItems) {
                                                    orderFoodList.remove(menuItem)
                                                    if (orderFoodList.isEmpty()) {
                                                        btnProceedToCart.visibility = View.GONE
                                                        RestaurantMenuAdapter.cartEmpty = true
                                                    }
                                                }

                                            })
                                    recyclerRestaurantMenu.adapter = recyclerAdapter
                                    recyclerRestaurantMenu.layoutManager = layoutManager
                                }

                            } else {
                                Toast.makeText(
                                    this@RestaurantMenuActivity,
                                    " Error has occured",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }


                    }, Response.ErrorListener {
                        println(" Error is $it")
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {

                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["Token"] = "6b26ac55c5e989"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = android.app.AlertDialog.Builder(this)
            dialog.setTitle(" Error ")
            dialog.setMessage(" Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { _, _ ->

                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)// open settings
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this) // this code is used to finish the app at any moment
            }
            dialog.create()
            dialog.show()
        }
    }
//    }

    private fun setUpToolbar(title: String) {
        setSupportActionBar(resMenuToolbar)
        supportActionBar?.title = title
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!RestaurantMenuAdapter.cartEmpty) {
            val builder = AlertDialog.Builder(this@RestaurantMenuActivity)
                .setTitle("Confirmation")
                .setMessage("Going back will reset your cart.Do you still want to proceed?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent = Intent(this@RestaurantMenuActivity, MainActivity::class.java)
                    startActivity(intent)
                    RestaurantMenuAdapter.cartEmpty = true
                }
                .setNegativeButton("No") { _, _ ->
                    //Do Nothing
                }
                .create()
                .show()
        } else {
            val intent = Intent(this@RestaurantMenuActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
// TODO: class responsible for handling opeartion on data stored in database

    class ItemsInCart(
        context: Context,
        val restaurantId: String,
        val orderItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "res_db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(CartEntity(restaurantId, orderItems))
                    db.close()
                    return true
                }
                2 -> {
                    db.orderDao().deleteOrders(CartEntity(restaurantId, orderItems))
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}






















