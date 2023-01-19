package com.codewithharsh.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.adapter.HomeRecyclerAdapter
import com.codewithharsh.foodrunner.model.Restaurants
import com.codewithharsh.foodrunner.util.ConnectionManager
import org.json.JSONException


class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var restaurantList = arrayListOf<Restaurants>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)

        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        restaurantSetup()

        return view
    }


    fun restaurantSetup(){
        //Volley request
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, null,
                Response.Listener {
                    println("Response is $it")

                    try {
                        progressLayout.visibility = View.GONE
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            val data = jsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Restaurants(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantList.add(restaurantObject)
                                recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
//                        Toast.makeText(activity as Context, "Some Unexpected Error Occurred !!!!", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
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
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val openSettings = Intent(Settings.ACTION_WIFI_SETTINGS)
//                val openSettings = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(openSettings)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, Listner ->
                    ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }


}