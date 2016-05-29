package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.scoup.ScoupImplicits
import com.themillhousegroup.witchhunt.{ RuleEnumerator, UnusedSelectorViolation, Violation, ViolationType }
import org.jsoup.nodes.Document
import scala._
import scala.Some
import com.themillhousegroup.witchhunt.Violation
import com.helger.css.decl.CSSDeclaration

object UnusedSelectorCheck extends WitchhuntViolationCheck with ScoupImplicits {

  // Return a violation if there is no element matching the selector in ANY of the supplied pages
  def checkSelector(implicit ruleSet: RuleEnumerator, selector: String, lineNumber: Int, declarationsWithin: Seq[CSSDeclaration], applicablePages: Set[Document]): Option[Violation] = {
    // As soon as we find an element that matches the selector, we can stop:
    applicablePages.find { stylePage =>
      stylePage.select(selector).nonEmpty
    }.fold(
      buildViolation(UnusedSelectorViolation)
    )(_ => None)
  }
}
