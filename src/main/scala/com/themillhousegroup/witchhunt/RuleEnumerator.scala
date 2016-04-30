package com.themillhousegroup.witchhunt

import com.helger.css.reader.CSSReader
import com.helger.css.ECSSVersion
import com.helger.css.decl._

import scala.collection.JavaConverters._
import com.helger.css.writer.CSSWriterSettings
import java.net.URL

object RuleEnumerator {
  val cssVersion = ECSSVersion.LATEST
  val writerSettings = new CSSWriterSettings(cssVersion)

  // Helper to eliminate CSS selectors that cannot be queried by JSoup - e.g. a:active
  def queryable(selector: CSSSelector): Boolean = {
    !selector.getAsCSSString(writerSettings, 0).contains(":")
  }
}

class RuleEnumerator(val cssContent: String, val sourceUrl: URL) {
  import RuleEnumerator._

  private val path = sourceUrl.getPath
  val sourceName = path.substring(path.lastIndexOf("/") + 1)
  val stylesheet: CascadingStyleSheet = CSSReader.readFromString(cssContent, cssVersion)

  private def toSelectorAndLineNumber(selector: CSSSelector): (String, Int) = {
    selector.getAsCSSString(writerSettings, 0) -> selector.getSourceLocation.getFirstTokenBeginLineNumber
  }

  private def toSelectorAndLineNumberSeq(styleRule: CSSStyleRule): Seq[(String, Int)] = {
    styleRule.getAllSelectors.asScala.filter(queryable).map(toSelectorAndLineNumber)
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
