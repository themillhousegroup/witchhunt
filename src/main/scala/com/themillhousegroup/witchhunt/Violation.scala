package com.themillhousegroup.witchhunt

import java.net.URL

sealed trait ViolationType {
  def describe(v: Violation): String
}

final object UnusedSelectorViolation extends ViolationType {
  def describe(v: Violation): String = s"Selector '${v.selector}' is unused"
}

/**
 * Represents a CSS Selector that had no applicable
 * elements in the inspected styleguide page(s)
 */
case class Violation(
    relativePath: String,
    url: URL,
    lineNumber: Int,
    selector: String,
    applicablePages: Set[String],
    violationType: ViolationType) {

  override lazy val toString: String = {
    s"$relativePath:$lineNumber - Selector '$selector' is unused"
  }
}
