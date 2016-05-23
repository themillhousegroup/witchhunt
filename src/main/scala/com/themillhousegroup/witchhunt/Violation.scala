package com.themillhousegroup.witchhunt

import java.net.URL

sealed trait ViolationType {
  def describe(v: Violation): String
}

/**
 * Represents a CSS Selector that had no applicable
 * elements in the inspected styleguide page(s)
 */
final object UnusedSelectorViolation extends ViolationType {
  def describe(v: Violation): String = s"Selector '${v.selector}' is unused"
}

/**
 * Represents a CSS Selector that is overly specific
 */
final object ExcessiveSpecificityViolation extends ViolationType {
  def describe(v: Violation): String = s"Selector '${v.selector}' is too specific - score ${v.violationValue.getOrElse("?")} exceeds threshold ${v.thresholdValue.getOrElse("?")}"
}

case class Violation(
    relativePath: String,
    url: URL,
    lineNumber: Int,
    selector: String,
    applicablePages: Set[String],
    violationType: ViolationType,
    thresholdValue: Option[Int] = None,
    violationValue: Option[Int] = None) {

  override lazy val toString: String = {
    violationType.describe(this)
  }
}
