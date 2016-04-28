package com.themillhousegroup.witchhunt

import org.specs2.mutable.Specification
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import java.net.URL

import com.themillhousegroup.witchhunt.test.GithubPagesHelper._

class StyleguideSpiderSpec extends Specification {

  def visit(target: URL) = Await.result(
    StyleguideSpider.visit(target), Duration(5, "seconds")
  )

  "Styleguide Spider" should {

    "be able to extract a single document from a plain styleguide" in {

      val result = visit(basicStyleguide)

      result must haveLength(1)

      result.head.title mustEqual ("Witchhunt by themillhousegroup")
    }

    "be able to follow links from the starting document" in {

      val result = visit(styleguideWithLink)

      result must haveLength(2)

      result.map(_.title) must contain("Page with local link", "Witchhunt by themillhousegroup")
    }

    "not follow circular links" in {

      val result = visit(styleguideWithCircularLink)

      result must haveLength(3)

      result.map(_.title) must contain("Page with multiple local links", "Page with local link", "Witchhunt by themillhousegroup")
    }
  }
}
