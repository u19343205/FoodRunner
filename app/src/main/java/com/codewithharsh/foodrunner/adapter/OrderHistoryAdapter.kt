package com.codewithharsh.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.model.MenuItems
import com.codewithharsh.foodrunner.model.OrderItemDetails
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(val context: Context, val orderHistoryList: ArrayList<OrderItemDetails>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_single_row, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orderHistoryObject = orderHistoryList[position]
        holder.tvHistoryResName.text = orderHistoryObject.resName
        holder.tvOrderDate.text = formatDate(orderHistoryObject.orderDate)
        setUpRecycler(holder.recyclerOrderHistoryDetail, orderHistoryObject)

    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHistoryResName: TextView = view.findViewById(R.id.tvHistoryResName)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val recyclerOrderHistoryDetail: RecyclerView =
            view.findViewById(R.id.recyclerOrderHistoryDetail)
    }


// here we use the same adapter to display item price as in cart activity to reduce the code
    private fun setUpRecycler(recyclerResHistory: RecyclerView,orderHistoryList: OrderItemDetails) {
        val foodItemList = ArrayList<MenuItems>()
        for (i in 0 until orderHistoryList.foodItems.length()) {
            val foodJson = orderHistoryList.foodItems.getJSONObject(i)
            foodItemList.add(MenuItems(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val cartItemAdapter = CartRecyclerAdapter(context, foodItemList)
        val mLayoutAdapter = LinearLayoutManager(context)
        recyclerResHistory.adapter = cartItemAdapter
        recyclerResHistory.layoutManager = mLayoutAdapter
//        recyclerResHistory.itemAnimator = DefaultItemAnimator()
    }

// got from internet
    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yy HH:MM:SS", Locale.ENGLISH)
        val date: Date = inputFormat.parse(dateString) as Date

        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
        return outputFormat.format(date)

    }
}