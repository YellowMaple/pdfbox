package dev.simplesolution

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PageMode
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.io.IOException


object ReadPages {
    private val listBookmarkItem = mutableListOf<BookmarkItem>()
    private val treeBookmarkItem = mutableListOf<BookmarkItem>()//解析之后的树状目录
    private const val chapter = "Chapter"
    private const val targetPdf = "Computer_Networking.pdf"
    private const val bias = 24 //前言等造成的实际页数偏移值

    @JvmStatic
    fun main(args: Array<String>) {
        try {
            PDDocument.load(File(targetPdf)).use { document ->
                val stripper = PDFTextStripper()
                stripper.startPage = 19//文本目录的起始页
                stripper.endPage = 25//文本目录的结束页
                val result = stripper.getText(document)
                val list = result.split("\n")
                val iterator = list.iterator()
                while (iterator.hasNext()) {//将文本的目录逐行转换成标准的BookMark
                    val next = iterator.next()
                    if ("""^\d+\.\d+""".toRegex().containsMatchIn(next) || chapter in next) {
                        if ("""\d$""".toRegex().containsMatchIn(next))//单行目录
                            genBookmark(next)
                        else { //跨行目录，先跨行合并
                            val joinString = next.trim() + iterator.next()
                            genBookmark(joinString)
                        }
                    }
                }
                listBookmarkItem.forEach {//将所有的目录开始分类，首先是一级目录——章，这些一级目录没有父类，不需要要加入到其他的父类的孩子之中
                    if (chapter in it.tittle) {  //just add to chapter
                        treeBookmarkItem.add(it)
                    } else {  // parse subchapter 否则就是各个子章节，都需要找到其父类，并加入父类的子章节数组内
                        addToParent(it)
                    }
                }
                val documentOutline = PDDocumentOutline()
                document.documentCatalog.documentOutline = documentOutline
                val pagesOutline = PDOutlineItem()
                pagesOutline.title = "Content"
                val pageDestination: PDPageDestination = PDPageFitWidthDestination()
                pageDestination.page = document.getPage(stripper.startPage - 1)
                pagesOutline.destination = pageDestination
                documentOutline.addLast(pagesOutline)
                treeBookmarkItem.forEach {
                    val chapterOutline = PDOutlineItem().apply { title = it.tittle + " " + it.content }
                    val chapterDestination: PDPageDestination = PDPageFitWidthDestination()
                    chapterDestination.page = document.getPage(it.page + bias)
                    chapterOutline.destination = chapterDestination
                    pagesOutline.addLast(chapterOutline)
                    if (it.children.size > 0) {//如果有子章节
                        it.children.forEach { child ->
                            val subChapterOutline = PDOutlineItem().apply { title = child.tittle + " " + child.content }
                            val subChapterDestination: PDPageDestination = PDPageFitWidthDestination()
                            subChapterDestination.page = document.getPage(child.page + bias)
                            subChapterOutline.destination = subChapterDestination
                            chapterOutline.addLast(subChapterOutline)
                            if (child.children.size > 0) {
                                child.children.forEach { grandChild ->
                                    val grandChildOutline =
                                        PDOutlineItem().apply { title = grandChild.tittle + " " + grandChild.content }
                                    val grandChildDestination = PDPageFitWidthDestination().apply {
                                        page = document.getPage(grandChild.page + bias)
                                    }
                                    grandChildOutline.destination = grandChildDestination
                                    subChapterOutline.addLast(grandChildOutline)
                                }
                            }
                        }
                    }
                }
                pagesOutline.openNode()
                documentOutline.openNode()
                document.documentCatalog.pageMode = PageMode.USE_OUTLINES
                document.save(targetPdf)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun addToParent(bookmark: BookmarkItem) {
        val subTittle = bookmark.tittle.split(".")
        val chapter = bookmark.tittle.split(".")[0]
        val subChapter = bookmark.tittle.split(".")[1]
        if (subTittle.size == 2) {
            treeBookmarkItem.forEach {
                if (it.tittle.split(" ")[1] contentEquals chapter)
                    it.children.add(bookmark)
            }
        }
        if (subTittle.size == 3) {
            treeBookmarkItem.forEach {
                if (it.tittle.split(" ")[1] contentEquals chapter) {//chapter
                    it.children.forEach { child ->
                        if (child.tittle.split(".")[1] contentEquals subChapter)
                            child.children.add(bookmark)
                    }
                }
            }
        }
    }

    private fun genBookmark(bookmark: String) {
        val splitContent = bookmark.split(" ")
        var content = ""
        val pageNumber = splitContent.size - 1
        if (chapter in splitContent[0])//chapter
        {
            content = splitContent.subList(2, splitContent.size - 2).joinToString(" ")
            listBookmarkItem.add(
                BookmarkItem(
                    splitContent[0] + " " + splitContent[1], content, splitContent[pageNumber].trim().toInt(),
                    mutableListOf()
                )
            )
        } else {
            content = splitContent.subList(1, splitContent.size - 2).joinToString(" ")
            listBookmarkItem.add(
                BookmarkItem(
                    splitContent[0], content, splitContent[pageNumber].trim().toInt(),
                    mutableListOf()
                )
            )
        }
    }
}