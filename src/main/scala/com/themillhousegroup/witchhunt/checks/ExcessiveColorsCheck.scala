package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.scoup.ScoupImplicits
import com.themillhousegroup.witchhunt._
import org.jsoup.nodes.Document
import com.helger.css.decl.{ CSSExpression, CSSDeclaration }

class ExcessiveColorsCheck(options: WitchhuntOptions) extends WitchhuntViolationCheck with ScoupImplicits {

  val CSS_COLOR_PROP = "color"

  val knownColors = scala.collection.mutable.Set[CSSExpression]()

  // Return a violation if the total number of colors defined exceeds the configured limit
  def checkSelector(implicit ruleSet: RuleEnumerator, selector: String, lineNumber: Int, declarationsWithin: Seq[CSSDeclaration], applicablePages: Set[Document]): Option[Violation] = {
    knownColors ++= declarationsWithin.filter(CSS_COLOR_PROP == _.getProperty).map { declaration =>
      declaration.getExpression
    }.toSet

    if (knownColors.size > options.colorLimit) {
      buildViolation(ExcessiveColorsViolation, Some(options.colorLimit), Some(knownColors.size))
    } else {
      None
    }
  }
}
