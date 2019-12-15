package com.yuvasai.redditapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CheckLogin {

    @SerializedName("json")
    @Expose
    var json: Json? = null

    override fun toString(): String {
        return "CheckLogin{" +
                "json=" + json +
                '}'.toString()
    }
}