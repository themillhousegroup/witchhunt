package com.themillhousegroup.witchhunt

import java.net.URL

import com.themillhousegroup.scoup.{ Scoup, ScoupImplicits }
import org.jsoup.nodes.Document

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Witchhunt extends ScoupImplicits {
  def inspect(styleguideUrl: URL): Future[Seq[String]] = {
    StyleguideScraper.visit(styleguideUrl).flatMap { stylePages =>

      val futureViolations = stylePages.toSeq.map { stylePage =>
        processPage(stylePage)
      }

      Future.sequence(futureViolations).map { violationLists =>
        violationLists.flatten
      }
    }
  }

  private def processPage(stylePage: Document): Future[Seq[String]] = {
    val pageUrl = new URL(stylePage.location)
    val stylesheets = CSSEnumerator.localStylesheetUrls(stylePage)
    val absUrls = stylesheets.map(StyleguideScraper.createFullLocalUrl(pageUrl))

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
