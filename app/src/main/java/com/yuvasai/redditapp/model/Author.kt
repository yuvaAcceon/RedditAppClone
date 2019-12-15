package com.yuvasai.redditapp.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "author", strict = false)
data class Author (

    @field:Element(required = false, name = "name")
    var name: String? = null,

    @field:Element(required = false, name = "uri")
    var uri: String? = null

) : Serializable {

    override fun toString(): String {
        return "Author{" +
                "name='" + name + '\''.toString() +
                ", uri='" + uri + '\''.toString() +
                '}'.toString()
    }
}