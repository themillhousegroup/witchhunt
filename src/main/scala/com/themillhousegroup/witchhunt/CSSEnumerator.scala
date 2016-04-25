package com.themillhousegroup.witchhunt

import com.themillhousegroup.scoup.{ ScoupImplicits, Scoup }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object CSSEnumerator extends CSSEnumerator("http://localhost:9000/styleguide")

/**
 * Parses a HTML document at `baseUrl`, finding stylesheet declarations within.
 */
class CSSEnumerator(val baseUrl: String) extends ScoupImplicits {

  def allStylesheetUrls: Future[Seq[String]] = {
    Scoup.parse(baseUrl).map { doc =>
      doc.head.select("link").filter { elem =>
        elem.attr("rel") == "stylesheet"
      }.map { elem =>
        elem.attr("href")
      }.toSeq
    }
  }

  def localStylesheetUrls: Future[Seq[String]] = {
    allStylesheetUrls.map { allUrls =>
      allUrls.filter(_.startsWith("/"))
    }
  }
}
