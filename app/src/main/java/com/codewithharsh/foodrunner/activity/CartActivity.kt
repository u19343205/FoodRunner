package com.codewithharsh.foodrunner.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.activity.RestaurantMenuActivity.Companion.resId
import com.codewithharsh.foodrunner.adapter.CartRecyclerAdapter
import com.codewithharsh.foodrunner.adapter.RestaurantMenuAdapter
import com.codewithharsh.foodrunner.database.CartDatabase
import com.codewithharsh.foodrunner.database.CartEntity
import com.codewithharsh.foodrunner.model.MenuItems
import com.codewithharsh.foodrunner.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var cartToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var tvResName: TextView
    private lateinit var btnConfirmOrder: Button

    private lateinit var recyclerCartItem: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var cartRecyclerAdapter: CartRecyclerAdapter

    private lateinit var cartProgressLayout: RelativeLayout
    private lateinit var cartProgressBar: ProgressBar

//    lateinit var sharedPreferences: SharedPreferences

    private var orderList = ArrayList<MenuItems>()
    private var resId: Int = 0
    private var resName: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        receiveData()
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder)
        setUpToolBar()

        setUpCartList()
        tvResName = findViewById(R.id.tvResName)
        tvResName.text = resName

        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].itemPrice.toInt()
        }
        val total = "Place order (Total: Rs.$sum)"
        btnConfirmOrder.text = total

        btnConfirmOrder.setOnClickListener {
            placeOrderRequest(sum.toString())
        }
    }

    private fun receiveData() {
        cartProgressLayout = findViewById(R.id.cartProgressLayout)
        cartProgressLayout.visibility = View.GONE
        tvResName = findViewById(R.id.tvResName)
        val bundle = intent.getBundleExtra("data")
        if (bundle != null) {
            resId = bundle.getInt("resId")
            resName = bundle.getString("resName")
        }
    }

    fun setUpToolBar() {
        cartToolbar = findViewById(R.id.cartToolbar)
        setSupportActionBar(cartToolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }


    // function for setting up the items list in cart activity we recieve the data which we have stored in database
    private fun setUpCartList() {
        recyclerCartItem = findViewById(R.id.recyclerCartItem)
        val dbList: List<CartEntity> = RetrieveCartItemsfromDBAsync(applicationContext).execute().get()

// here we are converting the string back to list in order to display
        for (elements in dbList) {
            orderList.addAll(
                Gson().fromJson(elements.foodItems, Array<MenuItems>::class.java).asList()
            )
        }

        if (orderList.isEmpty()) {
            cartProgressLayout.visibility = View.VISIBLE
        } else {
            cartProgressLayout.visibility = View.GONE
        }

        cartRecyclerAdapter = CartRecyclerAdapter(this@CartActivity, orderList)
        layoutManager = LinearLayoutManager(this@CartActivity)
        recyclerCartItem.adapter = cartRecyclerAdapter
        recyclerCartItem.layoutManager = layoutManager
    }

    // class for getting the data from the database /room
    class RetrieveCartItemsfromDBAsync(val context: Context) : AsyncTask<Void, Void, List<CartEntity>>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "res_db").build()

        override  fun doInBackground(vararg params: Void?): List<CartEntity> {
            return db.orderDao().getAllOrders()
        }
    }

    // function for placing the order
    fun placeOrderRequest(sum: String) {
        if (ConnectionManager().checkConnectivity(this@CartActivity)) {

            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val jsonParams = JSONObject()
            jsonParams.put("user_id", this@CartActivity.getSharedPreferences("FoodRunner", MODE_PRIVATE).getString("user_id", null) as String)

            val bundle = intent.getBundleExtra("data")
            resId = bundle?.getInt("resId", 0) as Int

//Parameters to be passed
            jsonParams.put("restaurant_id", resId.toString())
            jsonParams.put("total_cost", sum)
            val foodArray = JSONArray()
            for (i in 0 until orderList.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", orderList[i].id)
                foodArray.put(i, foodId)
            }
            jsonParams.put("food", foodArray)

            val jsonObjectRequest = object : JsonObjectRequest(Method.POST, url, jsonParams,
                Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
// if the order is placed successfully then at first we have to remove that data from the database
                                ClearDBAsync(this@CartActivity, resId.toString()).execute().get()
                            RestaurantMenuAdapter.cartEmpty = true
// for the confirmation of the order we display the dialog box of full screen size
                            val dialog = Dialog(
                                this@CartActivity,
                                android.R.style.Theme_Black_NoTitleBar_Fullscreen
                            )
                            dialog.setContentView(R.layout.order_placed)
                            dialog.show()
                            dialog.setCancelable(false)
                            val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                            btnOk.setOnClickListener {
                                dialog.dismiss()
                                startActivity(Intent(this@CartActivity, MainActivity::class.java))
                                ActivityCompat.finishAffinity(this@CartActivity)
                            }

                        } else {
//                            relativeCart.visibility = View.VISIBLE
                            Toast.makeText(
                                this@CartActivity,
                                "Some Error occurred",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()

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
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection not found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val openSettings = Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
                startActivity(openSettings)
                finish()
            }
            dialog.setNegativeButton("Cancel") { _, _ ->
                // Do Nothing
            }
        }
    }

    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "res_db").build()
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }
    }

    // this part is important because we must ensure that in case if the user presses back button the cart should be
    // empty because if then the user goes to some another restaurant orders there then app will get crashed  because
    // we have kept the restaurant id as primary key
    override fun onSupportNavigateUp(): Boolean {
        val clearCart = ClearDBAsync(applicationContext, resId.toString()).execute().get()
        RestaurantMenuAdapter.cartEmpty = false
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
ClearDBAsync(applicationContext, resId.toString()).execute().get()
        RestaurantMenuAdapter.cartEmpty = false
        val intent = Intent(this@CartActivity, RestaurantMenuActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}