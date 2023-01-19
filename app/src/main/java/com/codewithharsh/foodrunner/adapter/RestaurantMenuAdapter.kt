package com.codewithharsh.foodrunner.adapter

import android.content.Context
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.model.MenuItems

class RestaurantMenuAdapter(
    val context: Context,
    val menuList: ArrayList<MenuItems>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<RestaurantMenuAdapter.MenuViewHolder>() {

    companion object {
        var cartEmpty = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_menu_single_row, parent, false)
        return MenuViewHolder(view)
    }

    interface OnItemClickListener {
        fun onAddItemClick(menuItem: MenuItems)
        fun onRemoveItemClick(menuItem: MenuItems)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.tvDishName.text = menu.itemName
        val price = menu.itemPrice
        holder.tvCostForOneDish.text = menu.itemPrice
        holder.tvCostForOneDish.text = "Rs. $price"
        holder.tvSrNo.text = "${position + 1}"



        holder.btnAddToCart.setOnClickListener {
            holder.btnAddToCart.visibility = View.GONE
            holder.btnRemoveFromCart.visibility = View.VISIBLE
            listener.onAddItemClick(menu)
        }

        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility = View.GONE
            holder.btnAddToCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(menu)
        }

    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSrNo: TextView = view.findViewById(R.id.tvSrNo)
        val tvDishName: TextView = view.findViewById(R.id.tvDishName)
        val tvCostForOneDish: TextView = view.findViewById(R.id.tvCostForOneDish)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart: Button = view.findViewById(R.id.btnRemoveFromCart)
    }
}