package com.themillhousegroup.witchhunt

import org.specs2.mutable.Specification
import com.themillhousegroup.witchhunt.test.CSSHelpers._

class RuleEnumeratorSpec extends Specification {
  "Rule Enumerator" should {

    "be able to enumerate style rules in a CSS file" in {

      val styleEnumerator = new RuleEnumerator(simpleStyleRuleBlock, "simple.css")

      styleEnumerator.styleRules must not beEmpty

      styleEnumerator.styleRules must haveLength(3)

      styleEnumerator.styleRules must beEqualTo(
        Seq(
          "html" -> 1,
          "header" -> 6,
          "header span.green" -> 11
        ))

    }

    "be able to enumerate media rules in a CSS file" in {

      val styleEnumerator = new RuleEnumerator(simpleMediaRuleBlock, "media.css")

      styleEnumerator.mediaRules must not beEmpty

      styleEnumerator.mediaRules must haveLength(1)

      styleEnumerator.mediaRules must beEqualTo(
        Seq(
          ".radio-button" -> 2
        ))

    }
  }
}
