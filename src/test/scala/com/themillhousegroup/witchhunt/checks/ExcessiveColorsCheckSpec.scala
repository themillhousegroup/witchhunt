package com.themillhousegroup.witchhunt.checks

import com.themillhousegroup.witchhunt.{ RuleEnumerator, Violation, WitchhuntOptions }
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import com.helger.css.decl.{ CSSExpression, CSSDeclaration }

class ExcessiveColorsCheckSpec extends Specification with Mockito {

  val mockSourceFileName = "mock-source.css"
  val mockSourceFileLine = 24
  val colorDeclarations = Seq(new CSSDeclaration("color", CSSExpression.createSimple("white")))

  val re = mock[RuleEnumerator]
  re.sourceName returns mockSourceFileName

  def buildECC(threshold: Int) = {
    new ExcessiveColorsCheck(new WitchhuntOptions().copy(colorLimit = threshold))
  }

  "Excessive Colors Check" should {

    "Be silent for rules with no colors" in {
      val ecc = buildECC(1)

      ecc.checkSelector(re, "html", mockSourceFileLine, Nil, Set.empty) must beNone
    }

    "Be silent for rules with colors below the threshold" in {
      val ecc = buildECC(2)

      ecc.checkSelector(re, "html", mockSourceFileLine, colorDeclarations, Set.empty) must beNone
    }

    "Not report selectors with colors equal to the threshold" in {
      val ecc = buildECC(1)

      ecc.checkSelector(re, ".header", mockSourceFileLine, colorDeclarations, Set.empty) must beNone
    }

    "Report rules with colors above the threshold" in {
      val ecc = buildECC(0)

      val result = ecc.checkSelector(re, "html body div.header", mockSourceFileLine, colorDeclarations, Set.empty)

      result must beSome[Violation]

      result.getOrElse("").toString must beEqualTo(s"$mockSourceFileName:$mockSourceFileLine - Number of color declarations: 1 exceeds threshold 0")
    }
  }
}
