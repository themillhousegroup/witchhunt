package com.themillhousegroup.witchhunt

import com.themillhousegroup.scoup.{ Scoup, ScoupImplicits }
import org.jsoup.nodes.Document

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Parses an HTML document, finding stylesheet declarations within.
 */
object CSSEnumerator extends ScoupImplicits {

  def allStylesheetUrls(doc: Document): Seq[String] = {
    doc.head.select("link").filter { elem =>
      elem.attr("rel") == "stylesheet"
    }.map { elem =>
      elem.attr("href")
    }.toSeq
  }

  def localStylesheetUrls(doc: Document): Seq[String] = {
    allStylesheetUrls(doc).filter { url =>
      // It starts with a single-slash ONLY (a double-slash means protocol-relative"
      (url.startsWith("/") && !url.startsWith("//")) ||
        // It doesn't start with a traditional protocol specifier
        !(url.startsWith("http:") || url.startsWith("https://"))
    }
  }
}
