package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.witchhunt.{ExcessiveSpecificityViolation, RuleEnumerator, Violation, ViolationType}
import org.jsoup.nodes.Document
import com.helger.css.decl.CSSDeclaration

trait WitchhuntViolationCheck {
  def checkSelector(implicit ruleSet: RuleEnumerator, selector: String, lineNumber: Int, declarationsWithin: Seq[CSSDeclaration], applicablePages: Set[Document]): Option[Violation]

  protected def buildViolation[VT <: ViolationType](vt: VT,
                                                    thresholdValue:Option[Int] = None,
                                                    violationValue:Option[Int] = None)(implicit ruleSet: RuleEnumerator,
                                                                                       selector: String,
                                                                                       lineNumber: Int,
                                                                                       applicablePages: Set[Document]):Option[Violation] = {
    Some(
      Violation(
        ruleSet.sourceName,
        ruleSet.sourceUrl,
        lineNumber,
        selector,
        applicablePages.map(_.location),
        vt,
        thresholdValue,
        violationValue
      )
    )
  }
}
