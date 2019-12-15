package com.yuvasai.redditapp

import android.util.Log

class ExtractXML(private val xml: String, private val tag: String) {

    fun start(): ArrayList<String?> {
        val result = ArrayList<String?>()

        val splitXML =
            xml.split((tag + "\"").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val count = splitXML.size

        for (i in 1 until count) {
            var temp = splitXML[i]
            val index = temp.indexOf("\"")
            Log.d(TAG, "start: index: $index")
            Log.d(TAG, "start: extracted: $temp")

            temp = temp.substring(0, index)
            Log.d(TAG, "start: snipped: $temp")
            result.add(temp)
        }

        return result
    }

    companion object {

        private val TAG = "ExtractXML"
    }
}