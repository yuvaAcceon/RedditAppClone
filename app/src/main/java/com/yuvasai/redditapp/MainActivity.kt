package com.yuvasai.redditapp

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.yuvasai.redditapp.model.Feed
import com.yuvasai.redditapp.model.Post
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory


class MainActivity : AppCompatActivity(), ListItemClickListener {

    private val TAG = "MainActivity"

    private val BASE_URL = "https://www.reddit.com/r/"

    private var btnRefreshFeed: Button? = null
    private var mFeedName: EditText? = null
    private var currentFeed: String? = "popular"
    var posts = ArrayList<Post>()
    var favourites = ArrayList<String>()
    var customListAdapter: CustomListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()

        Log.d(TAG, "onCreate: starting.")
        btnRefreshFeed = findViewById(R.id.btnRefreshFeed)
        mFeedName = findViewById(R.id.etFeedName)

        init()


        btnRefreshFeed!!.setOnClickListener {
            val feedName = mFeedName!!.text.toString()
            if (feedName != "") {
                currentFeed = feedName
                init()
            } else {
                init()
            }
        }

    }

    private fun init() {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()

        val feedAPI = retrofit.create(FeedAPI::class.java)

        val call = feedAPI.getFeed(currentFeed ?: "")

        call.enqueue(object : Callback<Feed> {
            override fun onResponse(call: Call<Feed>, response: Response<Feed>) {
//                Log.d(TAG, "onResponse: feed: " + response.body().toString())
                Log.d(TAG, "onResponse: Server Response: $response")

                var entrys = response.body()?.entrys
                Log.d(TAG, "onResponse: entrys: " + response.body()?.entrys.toString())

                /*    Log.d(TAG, "onResponse: author: " + response.body()?.entrys?.get(0)?.author?.name.toString())
                Log.d(TAG, "onResponse: updated: " + response.body()?.entrys?.get(0)?.updated.toString())
                Log.d(TAG, "onResponse: title: " + response.body()?.entrys?.get(0)?.title.toString())*/


                for (i in entrys?.indices!!) {
                    val extractXML1 = ExtractXML(entrys[i].content!!, "<a href=")
                    val postContent = extractXML1.start()
                    val extractXML2 = ExtractXML(
                        entrys[i].content!!,
                        "<img src="
                    )
                    try {
                        postContent.add(extractXML2.start().get(0))
                        Log.d(TAG, "Thumbnail-$i " + extractXML2.start().get(0))
                    } catch (e: NullPointerException) {
                        postContent.add(null)
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail-$i):" + e.message)
                    } catch (e: IndexOutOfBoundsException) {
                        postContent.add(null)
                        Log.e(
                            TAG,
                            "onResponse: IndexOutOfBoundsException(thumbnail-$i):" + e.message
                        )
                    }

                    val lastPosition = postContent.size - 1
                    try {
                        posts.add(
                            Post(
                                entrys[i].title,
                                entrys[i].author!!.name?.substring(
                                    3,
                                    entrys[i].author!!.name?.length!!
                                ),
                                entrys[i].updated,
                                postContent[0],
                                postContent[lastPosition]
                            )
                        )
                    } catch (e: KotlinNullPointerException) {
                        posts.add(
                            Post(
                                entrys[i].title,
                                "None",
                                entrys[i].updated,
                                postContent[0],
                                postContent[lastPosition]
                            )
                        )
                        Log.e(TAG, "onResponse: NullPointerException: " + e.message)
                    }


                }

                for (j in posts.indices) {
                    Log.d(
                        TAG, "onResponse: \n " +
                                "PostURL: " + posts[j].postURL + "\n " +
                                "ThumbnailURL: " + posts[j].thumbnailURL + "\n " +
                                "Title: " + posts[j].title + "\n " +
                                "Author: " + posts[j].author + "\n " +
                                "updated: " + posts[j].date_updated + "\n "
                    )
                }

                val listView = findViewById<ListView>(R.id.listView)
                customListAdapter =
                    CustomListAdapter(this@MainActivity, R.layout.card_layout_main, posts)
                customListAdapter?.listItemClickListener = this@MainActivity
                listView.adapter = customListAdapter

            }

            override fun onFailure(call: Call<Feed>, t: Throwable) {
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.message)
                Toast.makeText(this@MainActivity, "An Error Occurred", Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        toolbar.setOnMenuItemClickListener { item ->
            Log.d(TAG, "onMenuItemClick: clicked menu item: $item")

            when (item.getItemId()) {

                R.id.navFavourites -> {
                    if (favourites.size != 0) {
                        val intent = Intent(this@MainActivity, FavouritesActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Please select Favourites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }

            false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onItemClicked(position: Int) {

        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Add to favourites?")
        builder1.setCancelable(true)
        builder1.setNeutralButton("OK", fun(dialog: DialogInterface?, i: Int) {

            if (favourites.size != 0) {
                retriveArray()
            }

            val gson = Gson()
            favourites.add(gson.toJson(posts[position]).toString())
            val sharedPrefs = getSharedPreferences("LOCAL_DATA", Context.MODE_PRIVATE)
            val editorCart = sharedPrefs.edit()
            val set = HashSet<String>()
            set.addAll(favourites)
            editorCart.putStringSet("FAVOURITES", set)
            editorCart.apply()
            dialog?.cancel()
            Toast.makeText(this@MainActivity, "Added to favourites", Toast.LENGTH_SHORT).show()

        })
        val alert11 = builder1.create()
        alert11.show()

    }

    private fun retriveArray() {
        val prefs = this.getSharedPreferences("LOCAL_DATA", Context.MODE_PRIVATE)
        val edit = prefs.edit()
        val set = prefs.getStringSet("FAVOURITES", null)
        val sample = ArrayList(prefs.getStringSet("FAVOURITES", null)!!)
        Log.d("Check Size", "Check Size" + sample.size)

        if (sample.size > 0) {
            for (i in sample.size - 1 downTo 0) {
                favourites.add(sample[i])
            }
        }
    }
}
