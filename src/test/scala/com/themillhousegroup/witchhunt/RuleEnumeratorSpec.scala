package com.themillhousegroup.witchhunt

import org.specs2.mutable.Specification
import com.themillhousegroup.witchhunt.test.CSSHelpers._

class RuleEnumeratorSpec extends Specification {
  "Rule Enumerator" should {

    "be able to enumerate style rules in a CSS file" in {

      val styleEnumerator = new RuleEnumerator(simpleStyleRuleBlock)

      styleEnumerator.styleRules must not beEmpty

      styleEnumerator.styleRules must haveLength(3)

      styleEnumerator.styleRules must beEqualTo(Seq("html", "header", "header span.green"))

    }
  }
}
