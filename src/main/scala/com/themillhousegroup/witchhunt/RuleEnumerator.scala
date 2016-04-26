package com.themillhousegroup.witchhunt

import com.helger.css.reader.CSSReader
import com.helger.css.decl.CSSStyleRule
import com.helger.css.ECSSVersion
import com.helger.css.decl._

import scala.collection.JavaConverters._
import com.helger.css.writer.CSSWriterSettings

class RuleEnumerator(val source: String, val sourceName: String) {

  val cssVersion = ECSSVersion.LATEST

  val writerSettings = new CSSWriterSettings(cssVersion)

  val stylesheet: CascadingStyleSheet = CSSReader.readFromString(source, cssVersion)

  private def toSelectorAndLineNumber(selector: CSSSelector): (String, Int) = {
    selector.getAsCSSString(writerSettings, 0) -> selector.getSourceLocation.getFirstTokenBeginLineNumber
  }

  private def toSelectorAndLineNumberSeq(styleRule: CSSStyleRule): Seq[(String, Int)] = {
    styleRule.getAllSelectors.asScala.map(toSelectorAndLineNumber)
  }

  lazy val styleRules: Seq[(String, Int)] = {
    stylesheet.getAllStyleRules.asScala.flatMap(toSelectorAndLineNumberSeq)
  }

  lazy val mediaRules: Seq[(String, Int)] = {
    stylesheet.getAllMediaRules.asScala.flatMap { mediaRule: CSSMediaRule =>
      mediaRule.getAllRules.asScala.flatMap {
        _ match {
          case (styleRule @ (_: CSSStyleRule)) => toSelectorAndLineNumberSeq(styleRule)
          case _ => Nil
        }
      }
    }
  }
}
