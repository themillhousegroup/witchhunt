package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.scoup.ScoupImplicits
import com.themillhousegroup.witchhunt._
import org.jsoup.nodes.Document

class ExcessiveSpecificityCheck(options: WitchhuntOptions) extends WitchhuntViolationCheck with ScoupImplicits {

  // Return a violation if the selector is more specific that the configured limit
  def checkSelector(ruleSet: RuleEnumerator, selector: String, lineNumber: Int, applicablePages: Set[Document]): Option[Violation] = {
    // As soon as we find an element that matches the selector, we can stop:

    val result = Specificity.calculateSingle(selector)

    if (result.asInt > options.specificityLimit) {
      Some(
        Violation(
          ruleSet.sourceName,
          ruleSet.sourceUrl,
          lineNumber,
          selector,
          applicablePages.map(_.location),
          ExcessiveSpecificityViolation,
          Some(options.specificityLimit),
          Some(result.asInt)
        )
      )
    } else {
      None
    }
  }
}
