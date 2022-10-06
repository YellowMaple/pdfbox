package dev.simplesolution
data class BookmarkItem(val tittle:String,val content:String,val page:Int,val children:MutableList<BookmarkItem>)