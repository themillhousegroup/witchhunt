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

  def visit(url: URL): Future[Set[Document]] = {
    visitLink(url, Set.empty)
  }

  private def visitLink(url: URL, alreadyVisited: Set[URL]): Future[Set[Document]] = {
    Scoup.parse(url.toString).flatMap { doc =>
      val links = doc.select("a").filter(isLocal).map(_.attr("href"))
      links.map(createFullLocalUrl(url)).filter(!alreadyVisited.contains(_)).foldLeft(Future.successful(Set(doc))) {
        case (acc, link) =>
          for {
            existingDocs <- acc
            newDocs <- visitLink(link, alreadyVisited + link)
          } yield (existingDocs ++ newDocs)
      }
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