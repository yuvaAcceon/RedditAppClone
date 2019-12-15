package com.yuvasai.redditapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.yuvasai.redditapp.model.Post


class FavouritesActivity : AppCompatActivity() {

    var favouriteStrings = ArrayList<String>()
    var favourites = ArrayList<Post>()
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)
        listView = findViewById(R.id.listViewFavs)
        retriveArray()

    }

    private fun retriveArray() {
        val prefs = this.getSharedPreferences("LOCAL_DATA", Context.MODE_PRIVATE)
        val edit = prefs.edit()
        val set = prefs.getStringSet("FAVOURITES", null)
        val sample = ArrayList(prefs.getStringSet("FAVOURITES", null)!!)
        Log.d("Check Size", "Check Size" + sample.size)

        if (sample.size > 0) {
            for (i in sample.size - 1 downTo 0) {
                var post = Gson().fromJson<Post>(sample[i], Post::class.java)
                favourites.add(post)
                Log.d("Array Value", "Array Value" + sample[i])
            }
            val customListAdapter =
                CustomListAdapter(this@FavouritesActivity, R.layout.card_layout_main, favourites)
            listView?.adapter = customListAdapter
        }
    }
}