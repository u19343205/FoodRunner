package com.codewithharsh.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.model.MenuItems

class CartRecyclerAdapter(val context: Context,  val cartList: ArrayList<MenuItems>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_item_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartList[position]
        holder.tvCartRestaurantName.text = cartItem.itemName
        val cost = "Rs.${cartItem.itemPrice}"
        holder.tvCartItemPrice.text = cost
    }

    override fun getItemCount(): Int {
        return cartList.size
    }


    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCartRestaurantName: TextView = view.findViewById(R.id.tvCartRestaurantName)
        val tvCartItemPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
    }
}