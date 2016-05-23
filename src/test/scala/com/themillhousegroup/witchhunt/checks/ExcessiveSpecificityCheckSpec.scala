package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.witchhunt.{ RuleEnumerator, Violation, WitchhuntOptions }
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class ExcessiveSpecificityCheckSpec extends Specification with Mockito {

  val re = mock[RuleEnumerator]

  def buildESC(threshold: Int) = {
    new ExcessiveSpecificityCheck(new WitchhuntOptions().copy(specificityLimit = threshold))
  }

  "Excessive Specificity Check" should {

    "Be silent for selectors below the threshold" in {
      val esc = buildESC(100)

      esc.checkSelector(re, "html", 24, Set.empty) must beNone
    }

    "Not report selectors with specificity equal to the threshold" in {
      val esc = buildESC(10)

      esc.checkSelector(re, ".header", 24, Set.empty) must beNone
    }

    "Report selectors with specificity above the threshold" in {
      val esc = buildESC(10)

      val result = esc.checkSelector(re, "html body div.header", 24, Set.empty)

      result must beSome[Violation]

      result.getOrElse("").toString must beEqualTo("Selector 'html body div.header' is too specific - score 13 exceeds threshold 10")
    }
  }
}
