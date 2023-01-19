package com.codewithharsh.foodrunner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.activity.RestaurantMenuActivity
import com.codewithharsh.foodrunner.database.CartDatabase
import com.codewithharsh.foodrunner.database.RestaurantEntity
import com.codewithharsh.foodrunner.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.tvRestaurantName.text = restaurant.name
        holder.tvCostForOne.text = restaurant.cost_for_one
        holder.tvRating.text = restaurant.rating
        holder.imgFavourite.setImageResource(R.drawable.ic_favourite_border)
        Picasso.get().load(restaurant.image_url).error(R.drawable.default_food_image)
            .into(holder.imgRestaurantImage)

// this code shows the changes if put the restaurant in the favourite list

        holder.imgFavourite.setOnClickListener {
            val favResEntity = RestaurantEntity(
                restaurant.id,
                restaurant.name,
                restaurant.rating,
                restaurant.cost_for_one,
                restaurant.image_url
            )

            if (!DBAsyncTask(context, favResEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, favResEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${holder.tvRestaurantName.text.toString()} added to Favourites",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_favourite)
                }
            } else {
                val async = DBAsyncTask(context, favResEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "${holder.tvRestaurantName.text.toString()} Removed from Favourite",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    holder.imgFavourite.setImageResource(R.drawable.ic_favourite_border)
                }
            }
        }
// this code shows whether the restaurant is stored in favourites or not using heart img
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()
        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.id)) {
            holder.imgFavourite.setImageResource(R.drawable.ic_favourite)
        } else {
            holder.imgFavourite.setImageResource(R.drawable.ic_favourite_border)

        }

// if we click on the cardview we go to menu activity
        holder.cvRestaurant.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("id", restaurant.id.toInt())
            intent.putExtra("name", restaurant.name)
            context.startActivity(intent)
            ActivityCompat.finishAffinity(context as Activity)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val tvRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val tvCostForOne: TextView = view.findViewById(R.id.tvCostForOne)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        val cvRestaurant: CardView = view.findViewById(R.id.cvRestaurant)
    }

    class DBAsyncTask(context: Context, val resEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, CartDatabase::class.java, "favRes-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            /*  Mode 1 -> Check DB if the Restaurant is favourite or not
                Mode 2 -> Save the Restaurant into DB as favourite
                Mode 3 -> Remove the favourite Restaurant */

            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity =
                        db.restaurantDao().getRestaurantById(resEntity.id)
                    db.close()
                    return restaurant != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(resEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(resEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    class GetAllFavAsyncTask(context: Context) : AsyncTask<Void, Void, List<String>>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "favRes-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {
            val list = db.restaurantDao().getAllRestaurants()
            val listOfFavId = arrayListOf<String>()
            for (i in list) {
                listOfFavId.add(i.id.toString())
            }
            return listOfFavId
        }

    }
}