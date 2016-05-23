package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.witchhunt.{RuleEnumerator, Violation, ViolationType}
import org.jsoup.nodes.Document

trait WitchhuntViolationCheck[VT <: ViolationType] {
  def checkSelector(ruleSet: RuleEnumerator, selector: String, lineNumber: Int, applicablePages: Set[Document]): Option[Violation[VT]]
}
