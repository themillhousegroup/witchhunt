package com.themillhousegroup.witchhunt

import org.jsoup.nodes.{ Element, Document }
import scala.concurrent.Future
import com.themillhousegroup.scoup.{ ScoupImplicits, Scoup }
import scala.concurrent.ExecutionContext.Implicits.global
import java.net.URL

/**
 * Scrapes / Spiders the given base URL (a styleguide), returning at least one Scoup/JSoup
 * Document, representing the styleguide and any local pages that link off it.
 */
object StyleguideScraper extends ScoupImplicits {

  def visit(url: URL): Future[Set[Document]] = {
    Scoup.parse(url.toString).map { doc =>
      val links = doc.select("a").filter(isLocal).map(_.attr("href"))
      links.map(createFullLocalUrl(url)).foldLeft(Set(doc)) {
        case (acc, link) =>
          //FIXME: handle recursion etc
          Set(doc)
      }
    }
  }

  private def isLocal(link: Element): Boolean = {
    val href = link.attr("href")
    href.startsWith("/")
  }

  private def createFullLocalUrl(base: URL)(link: String): String = {
    (new java.net.URL(base, link)).toString
  }
}
