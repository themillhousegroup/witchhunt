package com.themillhousegroup.witchhunt

import java.net.URL

import com.themillhousegroup.scoup.{ Scoup, ScoupImplicits }
import com.themillhousegroup.witchhunt.util.MapInverter
import org.jsoup.nodes.Document

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Witchhunt extends ScoupImplicits {
  def inspect(styleguideUrl: URL): Future[Seq[String]] = {
    StyleguideSpider.visit(styleguideUrl).flatMap { stylePages =>

      // For each page, list the stylesheets it references:
      val pageStylesheets: Map[Document, Set[URL]] = stylePages.toSeq.map { stylePage =>
        stylesheetsForPage(stylePage)
      }.toMap

      // Invert this so that for each stylesheet, we have a list of pages where it is used:
      val stylesheetPages: Map[URL, Set[Document]] = MapInverter.invert(pageStylesheets)

      // For each stylesheet, check its rules on pages where it is referenced:
      val iterableOfFutures = stylesheetPages.map {
        case (stylesheet, pages) =>
          fetchRules(stylesheet).map { ruleSet =>
            checkRuleSet(ruleSet, pages)
          }
      }

      // Twist this into what we need:
      Future.sequence(iterableOfFutures).map(_.flatten.toSeq)
    }
  }

  private def stylesheetsForPage(stylePage: Document): (Document, Set[URL]) = {
    val pageUrl = new URL(stylePage.location)
    val stylesheets = StylesheetFinder.localStylesheetUrls(stylePage)
    val absUrls = stylesheets.map(StyleguideSpider.createFullLocalUrl(pageUrl))

    stylePage -> absUrls.toSet
  }

  private def fetchRules(stylesheet: URL): Future[RuleEnumerator] = {
    Scoup.get(stylesheet.toString).map { ssBody =>
      new RuleEnumerator(ssBody, stylesheet.toString)
    }
  }

  // This is the key to it all. Returns a list of violations
  private def checkRuleSet(ruleSet: RuleEnumerator, applicablePages: Set[Document]): Seq[String] = {
    ruleSet.styleRules.flatMap { rule =>
      val selector = rule._1
      val lineNumber = rule._2

      checkSelector(ruleSet, selector, lineNumber, applicablePages)
    }
  }

  // Return a violation if there is no element matching the selector in ANY of the supplied pages
  private def checkSelector(ruleSet: RuleEnumerator, selector: String, lineNumber: Int, applicablePages: Set[Document]): Option[String] = {
    // As soon as we find an element that matches the selector, we can stop:
    applicablePages.find { stylePage =>
      stylePage.select(selector).nonEmpty
    }.fold[Option[String]](
      Some(s"Selector: '${selector}' (${ruleSet.sourceName}:${lineNumber}) - no match in ${applicablePages.map(_.location).mkString(", ")}")
    )(_ => None)
  }
}
