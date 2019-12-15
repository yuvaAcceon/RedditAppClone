package com.yuvasai.redditapp.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import java.io.Serializable


@Root(name = "feed", strict = false)
data class Feed (

    @field:Element(required = false, name = "icon")
    var icon: String? = null,

    @field:Element(required = false, name = "id")
    var id: String? = null,

    @field:Element(required = false, name = "logo")
    var logo: String? = null,

    @field:Element(required = false, name = "title")
    var title: String? = null,

    @field:Element(required = false, name = "updated")
    var updated: String? = null,

    @field:Element(required = false, name = "subtitle")
    var subtitle: String? = null,

    @field:ElementList(inline = true, name = "entry", entry = "entry", required = false)
    var entrys: List<Entry>? = null

) : Serializable{

    override fun toString(): String {
        return "Feed: \n [Entrys: \n$entrys]"
    }
}

