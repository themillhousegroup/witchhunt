package com.themillhousegroup.witchhunt

import java.net.URL

sealed trait ViolationType {
  def describe(v: Violation[_]): String
}

/**
  * Represents a CSS Selector that had no applicable
  * elements in the inspected styleguide page(s)
  */
final object UnusedSelectorViolation extends ViolationType {
  def describe(v: Violation[_]): String = s"Selector '${v.selector}' is unused"
}


case class Violation[VT <: ViolationType] (
    relativePath: String,
    url: URL,
    lineNumber: Int,
    selector: String,
    applicablePages: Set[String],
    violationType: VT) {

  override lazy val toString: String = {
    s"$relativePath:$lineNumber - Selector '$selector' is unused"
  }
}
