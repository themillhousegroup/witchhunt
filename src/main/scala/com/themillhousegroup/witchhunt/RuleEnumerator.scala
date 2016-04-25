package com.themillhousegroup.witchhunt

import com.helger.css.reader.CSSReader
import com.helger.css.ECSSVersion
import com.helger.css.decl.{ CSSSelector, CSSStyleRule, CascadingStyleSheet }
import scala.collection.JavaConverters._
import com.helger.css.writer.CSSWriterSettings

class RuleEnumerator(val source: String) {

  val cssVersion = ECSSVersion.LATEST

  val writerSettings = new CSSWriterSettings(cssVersion)

  val stylesheet: CascadingStyleSheet = CSSReader.readFromString(source, cssVersion)

  lazy val styleRules: Seq[String] = {
    stylesheet.getAllStyleRules.asScala.flatMap { styleRule: CSSStyleRule =>
      styleRule.getAllSelectors.asScala.map { selector: CSSSelector =>
        selector.getAsCSSString(writerSettings, 0)
      }
    }
  }

  lazy val fontFaceRules: Seq[String] = {
    Nil
  }

  lazy val mediaRules: Seq[String] = {
    Nil
  }
}
