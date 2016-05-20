package com.themillhousegroup.witchhunt

import java.net.URL

import com.themillhousegroup.scoup.{ Scoup, ScoupImplicits }
import com.themillhousegroup.witchhunt.util.MapInverter
import org.jsoup.nodes.Document

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class WitchhuntOptions(includeMediaRules: Boolean = false,
    initialPageOnly: Boolean = false,
    ignoreSheetNames: Seq[String] = Nil) {
  def filterMediaRules(enumerator: RuleEnumerator): Seq[(String, Int)] = {
    if (includeMediaRules) {
      enumerator.mediaRules
    } else {
      Nil
    }
  }

  def filterSheetNames(sheetUrl: URL): Boolean = {
    val sheetName = sheetUrl.getPath.split("/").last
    !ignoreSheetNames.contains(sheetName)
  }
}

object Witchhunt extends ScoupImplicits {
  def inspect(styleguideUrl: URL, options: WitchhuntOptions = WitchhuntOptions()): Future[Seq[Violation]] = {
    StyleguideSpider.visit(styleguideUrl, options.initialPageOnly).flatMap { stylePages =>

      // For each page, list the stylesheets it references:
      val pageStylesheets: Map[Document, Set[URL]] = stylePages.toSeq.map { stylePage =>
        stylesheetsForPage(stylePage)
      }.toMap

      // Invert this so that for each stylesheet, we have a list of pages where it is used:
      val stylesheetPages: Map[URL, Set[Document]] = MapInverter.invert(pageStylesheets)

      // For each stylesheet, check its rules on pages where it is referenced:
      val iterableOfFutures = stylesheetPages.filterKeys(options.filterSheetNames).map {
        case (stylesheet, pages) =>
          fetchRules(stylesheet).map { ruleEnumerator =>

            val ruleSet = ruleEnumerator.styleRules ++ options.filterMediaRules(ruleEnumerator)

            checkRuleSet(ruleEnumerator, ruleSet, pages)
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
    Scoup.get(stylesheet.toString).map { ssContent =>
      new RuleEnumerator(ssContent, stylesheet)
    }
  }

  // This is the key to it all. Returns a list of violations
  private def checkRuleSet(enumerator: RuleEnumerator, ruleSet: Seq[(String, Int)], applicablePages: Set[Document]): Seq[Violation] = {
    ruleSet.flatMap { rule =>
      val selector = rule._1
      val lineNumber = rule._2

      checkSelector(enumerator, selector, lineNumber, applicablePages)
    }
  }

  // Return a violation if there is no element matching the selector in ANY of the supplied pages
  private def checkSelector(ruleSet: RuleEnumerator, selector: String, lineNumber: Int, applicablePages: Set[Document]): Option[Violation] = {
    // As soon as we find an element that matches the selector, we can stop:
    applicablePages.find { stylePage =>
      stylePage.select(selector).nonEmpty
    }.fold[Option[Violation]](
      Some(
        Violation(
          ruleSet.sourceName,
          ruleSet.sourceUrl,
          lineNumber,
          selector,
          applicablePages.map(_.location),
          UnusedSelectorViolation
        )
      )
    )(_ => None)
  }
}
