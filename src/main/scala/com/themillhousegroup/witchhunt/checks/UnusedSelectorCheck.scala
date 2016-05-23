package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.scoup.ScoupImplicits
import com.themillhousegroup.witchhunt.{ RuleEnumerator, UnusedSelectorViolation, Violation }
import org.jsoup.nodes.Document

object UnusedSelectorCheck extends WitchhuntViolationCheck with ScoupImplicits {

  // Return a violation if there is no element matching the selector in ANY of the supplied pages
  def checkSelector(ruleSet: RuleEnumerator, selector: String, lineNumber: Int, applicablePages: Set[Document]): Option[Violation] = {
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
