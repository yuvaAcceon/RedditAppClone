package com.yuvasai.redditapp

import com.yuvasai.redditapp.model.CheckLogin
import com.yuvasai.redditapp.model.Feed
import retrofit2.Call
import retrofit2.http.*


interface FeedAPI {

  /*  @GET("popular/.rss")
    fun getFeed() : Call<Feed>*/

    //Non-static feed name
    @GET("{feed_name}/.rss")
    fun getFeed(@Path("feed_name") feed_name: String): Call<Feed>

    @POST("{user}")
    fun signIn(
        @HeaderMap headers: Map<String, String>,
        @Path("user") username: String,
        @Query("user") user: String,
        @Query("passwd") password: String,
        @Query("api_type") type: String
    ): Call<CheckLogin>


    companion object {

        val BASE_URL = "https://www.reddit.com/r/"
    }
}