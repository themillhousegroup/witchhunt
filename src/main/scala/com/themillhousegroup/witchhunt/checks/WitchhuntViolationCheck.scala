package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.witchhunt.{ RuleEnumerator, Violation, ViolationType }
import org.jsoup.nodes.Document
import com.helger.css.decl.CSSDeclaration

trait WitchhuntViolationCheck {
  def checkSelector(ruleSet: RuleEnumerator, selector: String, lineNumber: Int, declarationsWithin: Seq[CSSDeclaration], applicablePages: Set[Document]): Option[Violation]
}
