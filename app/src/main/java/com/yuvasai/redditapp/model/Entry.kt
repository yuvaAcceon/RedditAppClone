package com.yuvasai.redditapp.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import java.io.Serializable

@Root(name = "entry", strict = false)
data class Entry @JvmOverloads constructor (

    @field:Element(required = false, name = "content")
    var content: String? = null,

    @field:Element(required = false, name = "author")
    var author: Author? = null,

    @field:Element(required = false,name = "id")
    var id: String? = null,

    @field:Element(required = false, name = "title")
    var title: String? = null,

    @field:Element(required = false, name = "updated")
    var updated: String? = null

) : Serializable {

    override fun toString(): String {
        return "\n\nEntry{" +
                "content='" + content + '\''.toString() +
                ", author='" + author + '\''.toString() +
                ", id='" + id + '\''.toString() +
                ", title='" + title + '\''.toString() +
                ", updated='" + updated + '\''.toString() +
                '}'.toString() + "\n" +
                "--------------------------------------------------------------------------------------------------------------------- \n"
    }
}