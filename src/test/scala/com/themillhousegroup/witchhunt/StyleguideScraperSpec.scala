package com.themillhousegroup.witchhunt

import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.net.URL

class StyleguideScraperSpec extends Specification {

  val basicStyleguide = new URL("http://themillhousegroup.github.io/witchhunt/index.html  ")
  val styleguideWithLink = new URL("http://themillhousegroup.github.io/witchhunt/page-with-local-links.html")

  def visit(target: URL) = Await.result(
    StyleguideScraper.visit(target), Duration(15, "seconds")
  )

  "Styleguide Scraper" should {

    //    "be able to extract a single document from a plain styleguide" in {
    //
    //      val result = visit(basicStyleguide)
    //
    //      result must haveLength(1)
    //
    //      result.head.select("title").text mustEqual ("Witchhunt by themillhousegroup")
    //    }

    "be able to follow links from the starting document" in {

      val result = visit(styleguideWithLink)

      result must haveLength(2)

      result.map(_.select("title").text) mustEqual (Set("Page with local link", "Witchhunt by themillhousegroup"))
    }
  }
}
