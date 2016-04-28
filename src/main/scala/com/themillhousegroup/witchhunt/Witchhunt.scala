package com.themillhousegroup.witchhunt

import java.net.URL

import com.themillhousegroup.scoup.{ Scoup, ScoupImplicits }
import org.jsoup.nodes.Document

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Witchhunt extends ScoupImplicits {
  def inspect(styleguideUrl: URL): Future[Seq[String]] = {
    StyleguideSpider.visit(styleguideUrl).flatMap { stylePages =>

      // For each page, list the stylesheets it references:
      val pageStylesheets = stylePages.toSeq.map { stylePage =>
        stylesheetsForPage(stylePage)
      }

      // Invert this so that for each stylesheet, we have a list of pages where it is used:
      val stylesheetPages = pageStylesheets.flatMap {
        case (page, stylesheets) =>
          stylesheets.map { stylesheet =>
            stylesheet -> page
          }
      }.groupBy {
        case (stylesheet, page) =>
          stylesheet
      }.map {
        case (stylesheet, stylesheetPageList) =>
          stylesheet -> stylesheetPageList.map {
            case (stylesheet, page) =>
              page
          }.toSet
      }

      stylesheetPages.map {
        case (stylesheet, pages) =>
          val rules = fetchRules(stylesheet)

      }

      Future.sequence(futureViolations).map { violationLists =>
        violationLists.flatten
      }
    }
  }

  private def stylesheetsForPage(stylePage: Document) = {
    val pageUrl = new URL(stylePage.location)
    val stylesheets = StylesheetFinder.localStylesheetUrls(stylePage)
    val absUrls = stylesheets.map(StyleguideSpider.createFullLocalUrl(pageUrl))

    stylePage -> absUrls
  }

  private def processPage(stylePage: Document): Future[Seq[String]] = {
    val pageUrl = new URL(stylePage.location)
    val stylesheets = StylesheetFinder.localStylesheetUrls(stylePage)
    val absUrls = stylesheets.map(StyleguideSpider.createFullLocalUrl(pageUrl))

    fetchRules(absUrls).map { ruleSets =>
      ruleSets.flatMap { ruleSet =>
        checkRuleSet(stylePage, ruleSet)
      }
    }
  }

  private def fetchRules(absUrls: Seq[URL]): Future[Seq[RuleEnumerator]] = {
    Future.sequence(absUrls.map { stylesheet =>
      Scoup.get(stylesheet.toString).map { ssBody =>
        new RuleEnumerator(ssBody, stylesheet.toString)
      }
    })
  }

  private def fetchRules(stylesheet: URL): Future[RuleEnumerator] = {
    Scoup.get(stylesheet.toString).map { ssBody =>
      new RuleEnumerator(ssBody, stylesheet.toString)
    }
  }

  // This is the key to it all. Returns a list of violations
  private def checkRuleSet(stylePage: Document, ruleSet: RuleEnumerator): Seq[String] = {
    ruleSet.styleRules.flatMap { rule =>
      val selector = rule._1
      if (stylePage.select(selector).isEmpty) {
        Some(s"Rule: '${rule._1}' (${ruleSet.sourceName}:${rule._2}) - no match in ${stylePage.location}")
      } else None

    }
  }
}
