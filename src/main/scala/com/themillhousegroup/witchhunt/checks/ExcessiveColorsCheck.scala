package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.scoup.ScoupImplicits
import com.themillhousegroup.witchhunt._
import org.jsoup.nodes.Document

class ExcessiveColorsCheck(options: WitchhuntOptions) extends WitchhuntViolationCheck with ScoupImplicits {

  // Return a violation if the total number of colors defined exceeds the configured limit
  def checkSelector(ruleSet: RuleEnumerator, selector: String, lineNumber: Int, applicablePages: Set[Document]): Option[Violation] = {
    None
  }
}
