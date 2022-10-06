package dev.simplesolution

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PageMode
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitWidthDestination
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem
import java.io.IOException

object AddBookmarkPdfDocument {
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            PDDocument().use { document ->
                for (i in 0..9) {
                    document.addPage(PDPage())
                }
                val documentOutline = PDDocumentOutline()
                document.documentCatalog.documentOutline = documentOutline
                val pagesOutline = PDOutlineItem()
                pagesOutline.title = "All Pages"
//                pagesOutline.destination
                documentOutline.addLast(pagesOutline)
                for (i in 0 until document.numberOfPages) {
                    val pageDestination: PDPageDestination = PDPageFitWidthDestination()
                    pageDestination.page = document.getPage(i)
                    val bookmark = PDOutlineItem()
                    bookmark.destination = pageDestination
                    bookmark.title = "Document Page " + (i + 1)
                    pagesOutline.addLast(bookmark)
                }
                pagesOutline.openNode()
                documentOutline.openNode()
                document.documentCatalog.pageMode = PageMode.USE_OUTLINES
                document.save("BookmarkDocument.pdf")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}