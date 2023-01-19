package com.codewithharsh.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.adapter.OrderHistoryAdapter
import com.codewithharsh.foodrunner.model.OrderItemDetails
import com.codewithharsh.foodrunner.util.ConnectionManager
import org.json.JSONException

class OrderHistoryFragment : Fragment() {

    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var recyclerOrderHistory: RecyclerView
    private lateinit var rlNoOrderFound: RelativeLayout
    private lateinit var historyProgressLayout: RelativeLayout
    private lateinit var llOrders: LinearLayout

    private var orderHistoryList = ArrayList<OrderItemDetails>()
    private lateinit var sharedPreferences: SharedPreferences
    private var userId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        llOrders = view.findViewById(R.id.llOrders)
        rlNoOrderFound = view.findViewById(R.id.rlNoOrderFound)
        historyProgressLayout = view.findViewById(R.id.historyProgressLayout)
        historyProgressLayout.visibility = View.VISIBLE //1

        sharedPreferences =
            (activity as Context).getSharedPreferences("FoodRunner", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("user_id", null) as String

        llOrders.visibility = View.VISIBLE
//        rlNoOrderFound.visibility = View.VISIBLE

        sendHistoryRequest(userId)
        return view
    }

    private fun sendHistoryRequest(userId: String) {

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/"

            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url + userId,
                null,
                Response.Listener {
                    println("Response is $it")
                    historyProgressLayout.visibility = View.GONE //1
                    try {
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            val resArray = jsonObject.getJSONArray("data")
                            if (resArray.length() == 0) {
                                llOrders.visibility = View.GONE
                                rlNoOrderFound.visibility = View.VISIBLE
                            } else {
                                for (i in 0 until resArray.length()) {
                                    val orderObject = resArray.getJSONObject(i)
                                    val foodItemArray = orderObject.getJSONArray("food_items")
                                    val orderDetails = OrderItemDetails(
                                        orderObject.getString("order_id"),
                                        orderObject.getString("restaurant_name"),
                                        orderObject.getString("order_placed_at"),
                                        foodItemArray
                                    )
                                    orderHistoryList.add(orderDetails)
                                    if (orderHistoryList.isEmpty()) {
                                        llOrders.visibility = View.GONE
                                        rlNoOrderFound.visibility = View.VISIBLE
                                    } else {
                                        llOrders.visibility = View.VISIBLE
                                        rlNoOrderFound.visibility = View.GONE
                                        if (activity != null) {
                                            orderHistoryAdapter =
                                                OrderHistoryAdapter(
                                                    activity as Context,
                                                    orderHistoryList
                                                )

                                            val mLayoutManager =
                                                LinearLayoutManager(activity as Context)
                                            recyclerOrderHistory.adapter = orderHistoryAdapter
                                            recyclerOrderHistory.layoutManager = mLayoutManager
                                            recyclerOrderHistory.itemAnimator =
                                                DefaultItemAnimator()

                                        } else {
                                            queue.cancelAll(this::class.java.simpleName)
                                        }
                                    }
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    println("Error is $it")
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley Error Occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["Token"] = "6b26ac55c5e989"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not found")
            dialog.setPositiveButton("Open Settings") { _, _ ->
                val openSettings = Intent(Settings.ACTION_WIFI_SETTINGS)
//                val openSettings = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(openSettings)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }
}