package com.themillhousegroup.witchhunt

import com.helger.css.reader.CSSReader
import com.helger.css.ECSSVersion
import com.helger.css.decl.{ CSSStyleRule, CascadingStyleSheet }
import scala.collection.JavaConverters._
import com.helger.css.writer.CSSWriterSettings

class RuleEnumerator(val source: String) {

  val cssVersion = ECSSVersion.LATEST

  val writerSettings = new CSSWriterSettings(cssVersion)

  val stylesheet: CascadingStyleSheet = CSSReader.readFromString(source, cssVersion)

  def styleRules(): Seq[String] = {
    stylesheet.getAllStyleRules.asScala.map { styleRule: CSSStyleRule =>
      styleRule.getAsCSSString(writerSettings, 0)
    }
  }

  def fontFaceRules(): Seq[String] = {
    Nil
  }

  def mediaRules(): Seq[String] = {
    Nil
  }
}
