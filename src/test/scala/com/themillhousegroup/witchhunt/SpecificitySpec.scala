package com.themillhousegroup.witchhunt

import org.specs2.mutable.Specification

class SpecificitySpec extends Specification {
  "Specificity calculator" should {

    def checkCalculateSingle(selector: String, expectedIdCount: Int, expectedClassCount: Int, expectedElemCount: Int) = {
      Specificity.calculateSingle(selector) must beEqualTo(SpecificityResult(selector, expectedIdCount, expectedClassCount, expectedElemCount))
    }

    "Correctly calculate the specificity of a selector with a single #id" in {
      checkCalculateSingle("#foo", 1, 0, 0)
    }

    "Correctly calculate the specificity of a selector with a multiple #ids" in {
      checkCalculateSingle("#foo #bar", 2, 0, 0)
    }

    "Correctly calculate the specificity of a selector with a single .class" in {
      checkCalculateSingle(".foo", 0, 1, 0)
    }

    "Correctly calculate the specificity of a selector with multiple .classes" in {
      checkCalculateSingle(".foo .bar", 0, 2, 0)
    }

    "Correctly calculate the specificity of a selector with multiple non-separated .classes" in {
      checkCalculateSingle(".foo.bar", 0, 2, 0)
    }

    "Correctly calculate the specificity of a selector with a single element" in {
      checkCalculateSingle("strong", 0, 0, 1)
    }

    "Correctly calculate the specificity of a selector with multiple elements" in {
      checkCalculateSingle("a strong", 0, 0, 2)
    }

    "Correctly calculate the specificity of a selector with #ids and .classes" in {
      checkCalculateSingle("#foo .bar", 1, 1, 0)
    }

    "Correctly calculate the specificity of a selector with #ids and .classes - flipped order" in {
      checkCalculateSingle(".bar #foo", 1, 1, 0)
    }

    "Correctly calculate the specificity of a selector with multiple ids, classes and elements" in {
      checkCalculateSingle("footer.standard #links a strong", 1, 1, 3)
    }

    "Correctly handles attributes on selectors" in {
      checkCalculateSingle("input[type=hidden]", 0, 1, 1)
    }
  }
}
