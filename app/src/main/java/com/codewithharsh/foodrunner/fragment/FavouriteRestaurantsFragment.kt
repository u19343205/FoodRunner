package com.codewithharsh.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.codewithharsh.foodrunner.R
import com.codewithharsh.foodrunner.adapter.HomeRecyclerAdapter
import com.codewithharsh.foodrunner.database.CartDatabase
import com.codewithharsh.foodrunner.database.RestaurantEntity
import com.codewithharsh.foodrunner.model.Restaurants


class FavouriteRestaurantsFragment : Fragment() {

    lateinit var recyclerFavouriteRestaurant: RecyclerView
    lateinit var relativeFavorites: RelativeLayout
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayoutFav: RelativeLayout
    lateinit var progressBarFav: ProgressBar
    lateinit var rlNoFavorites: RelativeLayout
    lateinit var layoutManagerFav: RecyclerView.LayoutManager
    private var favRestaurantList = arrayListOf<Restaurants>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favourite_restaurants, container, false)

        relativeFavorites = view.findViewById(R.id.relativeFavorites)
        progressBarFav = view.findViewById(R.id.progressBarFav)
        progressLayoutFav = view.findViewById(R.id.progressLayoutFav)
        progressLayoutFav.visibility = View.VISIBLE
        rlNoFavorites = view.findViewById(R.id.rlNoFavorites)




        recyclerFavouriteRestaurant = view.findViewById(R.id.recyclerFavouriteRestaurant)
        recyclerAdapter = HomeRecyclerAdapter(activity as Context, favRestaurantList)
        layoutManagerFav = LinearLayoutManager(activity)
        recyclerFavouriteRestaurant.adapter = recyclerAdapter
        recyclerFavouriteRestaurant.layoutManager = layoutManagerFav
        val databaseList = RetrieveFavourites(activity as Context).execute().get()
        if (databaseList.isEmpty()) {
            progressLayoutFav.visibility = View.GONE
            rlNoFavorites.visibility = View.VISIBLE
        } else {
            rlNoFavorites.visibility = View.GONE
            progressLayoutFav.visibility = View.GONE
            relativeFavorites.visibility = View.VISIBLE
            for (i in databaseList) {
                //  we add the data to the restaurant list which is of data class restaurants like.
                favRestaurantList.add(
                    Restaurants(
                        i.id,
                        i.name,
                        i.rating,
                        i.cost_for_one,
                        i.image_url
                    )
                )
            }
        }
        return view
    }



    // class responsible for bringing the data back from the the database
    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {

        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db =
                Room.databaseBuilder(context, CartDatabase::class.java, "favRes-db").build()
            return db.restaurantDao().getAllRestaurants()
        }
    }


}