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
object StyleguideSpider extends ScoupImplicits {

  def visit(url: URL, thisPageOnly: Boolean = false): Future[Set[Document]] = {
    visitLink(url, Set.empty, thisPageOnly)
  }

  private def visitLink(url: URL, alreadyVisited: Set[URL], thisPageOnly: Boolean): Future[Set[Document]] = {
    Scoup.parse(url.toString).flatMap { doc =>

      if (thisPageOnly) {
        Future.successful(Set(doc))
      } else {
        visitLinks(url, doc, alreadyVisited)
      }
    }
  }

  private def visitLinks(url: URL, doc: Document, alreadyVisited: Set[URL]) = {
    val links = doc.select("a").filter(isLocal).map(_.attr("href"))
    links.map(createFullLocalUrl(url)).filter(!alreadyVisited.contains(_)).foldLeft(Future.successful(Set(doc))) {
      case (acc, link) =>
        for {
          existingDocs <- acc
          newDocs <- visitLink(link, alreadyVisited + link, false)
        } yield (existingDocs ++ newDocs)
    }
  }

  private def isLocal(link: Element): Boolean = {
    val href = link.attr("href")
    href.startsWith("/")
  }

  def createFullLocalUrl(base: URL)(link: String): URL = {
    (new java.net.URL(base, link))
  }
}
