package com.themillhousegroup.witchhunt

import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.net.URL

class StyleguideScraperSpec extends Specification {

  val basicStyleguide = new URL("http://themillhousegroup.github.io/witchhunt")

  "Styleguide Scraper" should {

    "be able to extract a single document from a plain styleguide" in {

      val result = Await.result(
        StyleguideScraper.visit(basicStyleguide),
        Duration(15, "seconds")
      )

      result must not beNull

      result must not beEmpty

      result must haveLength(1)

      result.head.select("title").text mustEqual ("Witchhunt by themillhousegroup")
    }
  }
}
