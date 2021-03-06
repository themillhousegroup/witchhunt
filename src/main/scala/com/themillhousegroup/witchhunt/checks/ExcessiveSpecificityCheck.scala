package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.scoup.ScoupImplicits
import com.themillhousegroup.witchhunt._
import org.jsoup.nodes.Document
import com.helger.css.decl.CSSDeclaration

class ExcessiveSpecificityCheck(options: WitchhuntOptions) extends WitchhuntViolationCheck with ScoupImplicits {

  // Return a violation if the selector is more specific that the configured limit
  def checkSelector(implicit ruleSet: RuleEnumerator, selector: String, lineNumber: Int, declarationsWithin: Seq[CSSDeclaration], applicablePages: Set[Document]): Option[Violation] = {

    val result = Specificity.calculateSingle(selector)

    if (result.asInt > options.specificityLimit) {
      buildViolation(
        ExcessiveSpecificityViolation,
        Some(options.specificityLimit),
        Some(result.asInt)
      )
    } else {
      None
    }
  }
}
