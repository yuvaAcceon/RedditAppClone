package com.yuvasai.redditapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Data {

    @SerializedName("modhash")
    @Expose
    var modhash: String? = null

    @SerializedName("cookie")
    @Expose
    var cookie: String? = null

    override fun toString(): String {
        return "Data{" +
                "modhash='" + modhash + '\''.toString() +
                ", cookie='" + cookie + '\''.toString() +
                '}'.toString()
    }
}