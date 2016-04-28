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
  selector: String)
