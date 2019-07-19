package lbs.whatsaround

import org.jsoup.Jsoup

class wikiScraper {

    fun fetchWikiImage(title: String): String {
        val site = "https://de.wikipedia.org/wiki/" + title
        val document = Jsoup.connect(site).get()
        val imageLink = document.select("a.image img").first()
        var url = imageLink!!.attr("abs:src")
        /*
        image.title = "Alexanderplatz"
        image.imageUrl = url
        println(image.title)
        return image
        */
        println(url)
        return url
    }

}