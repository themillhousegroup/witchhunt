package com.themillhousegroup.witchhunt

import java.net.URL

/**
 * Represents a CSS Selector that had no applicable
 * elements in the inspected styleguide page(s)
 */
case class Violation(
    relativePath: String,
    url: URL,
    lineNumber: Int,
    selector: String,
    applicablePages: Set[String]) {

  override lazy val toString: String = {
    s"$relativePath:$lineNumber - Selector '$selector' is unused"
  }
}
