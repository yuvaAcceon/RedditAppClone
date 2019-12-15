package com.yuvasai.redditapp.account

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.yuvasai.redditapp.FeedAPI
import com.yuvasai.redditapp.MainActivity
import com.yuvasai.redditapp.R
import com.yuvasai.redditapp.URLS
import com.yuvasai.redditapp.model.CheckLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private var mProgressBar: ProgressBar? = null
    private var mUsername: EditText? = null
    private var mPassword: EditText? = null


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate: started.")
        val btnLogin = findViewById<Button>(R.id.btn_login)
        mPassword = findViewById(R.id.input_password)
        mUsername = findViewById(R.id.input_username)
        mProgressBar = findViewById(R.id.loginRequestLoadingProgressBar)
        mProgressBar!!.visibility = View.GONE


        btnLogin.setOnClickListener {
            Log.d(TAG, "onClick: Attempting to log in.")
            val username = mUsername!!.text.toString()
            val password = mPassword!!.text.toString()

            if (username != "" && password != "") {
                mProgressBar!!.visibility = View.VISIBLE
                //method for signing in
                login(username, password)
            }
        }

    }

    private fun login(username: String, password: String) {

        val retrofit = Retrofit.Builder()
            .baseUrl(URLS.LOGIN_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val feedAPI = retrofit.create(FeedAPI::class.java)

        val headerMap = HashMap<String,String>()
        headerMap.put("Content-Type", "application/json")


        val call = feedAPI.signIn(headerMap, username, username, password, "json")

        call.enqueue(object : Callback<CheckLogin> {
            override fun onResponse(call: Call<CheckLogin>, response: Response<CheckLogin>) {
                try {
                    //Log.d(TAG, "onResponse: feed: " + response.body().toString());
                    Log.d(
                        TAG,
                        "onResponse: Server Response: $response"
                    )

                    val modhash = response.body()?.json?.data?.modhash
                    val cookie = response.body()?.json?.data?.cookie
                    Log.d(TAG, "onResponse: modhash: $modhash")
                    Log.d(TAG, "onResponse: cookie: $cookie")

                    if (modhash != "") {
                        setSessionParams(username, modhash!!, cookie!!)
                        mProgressBar?.setVisibility(View.GONE)
                        mUsername?.setText("")
                        mPassword?.setText("")
                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)

                        //navigate back to previous activity
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: NullPointerException) {
                    Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT)
                        .show()
                    mProgressBar?.setVisibility(View.GONE)
                    Log.e(TAG, "onResponse: NullPointerException: " + e.message)
                }

            }

            override fun onFailure(call: Call<CheckLogin>, t: Throwable) {
                mProgressBar?.setVisibility(View.GONE)
                Log.e(TAG, "onFailure: Unable to retrieve RSS: " + t.message)
                Toast.makeText(this@LoginActivity, "An Error Occured", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSessionParams(username: String, modhash: String, cookie: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
        val editor = preferences.edit()

        Log.d(
            TAG, "setSessionParams: Storing session variables:  \n" +
                    "username: " + username + "\n" +
                    "modhash: " + modhash + "\n" +
                    "cookie: " + cookie + "\n"
        )


        editor.putString("@string/SessionUsername", username)
        editor.commit()
        editor.putString("@string/SessionModhash", modhash)
        editor.commit()
        editor.putString("@string/SessionCookie", cookie)
        editor.commit()
    }

    companion object {

        private val TAG = "LoginActivity"
    }
}