package com.themillhousegroup.witchhunt

import com.helger.css.reader.CSSReader
import com.helger.css.ECSSVersion
import com.helger.css.decl._
import scala.collection.JavaConverters._
import com.helger.css.writer.CSSWriterSettings

class RuleEnumerator(val source: String, val sourceName: String) {

  val cssVersion = ECSSVersion.LATEST

  val writerSettings = new CSSWriterSettings(cssVersion)

  val stylesheet: CascadingStyleSheet = CSSReader.readFromString(source, cssVersion)

  lazy val styleRules: Seq[(String, Int)] = {
    stylesheet.getAllStyleRules.asScala.flatMap { styleRule: CSSStyleRule =>
      styleRule.getAllSelectors.asScala.map { selector: CSSSelector =>
        selector.getAsCSSString(writerSettings, 0) -> selector.getSourceLocation.getFirstTokenBeginLineNumber
      }
    }
  }

  lazy val mediaRules: Seq[(String, Int)] = {
    stylesheet.getAllMediaRules.asScala.flatMap { mediaRule: CSSMediaRule =>
      mediaRule.getAllRules.asScala.map { topLevelRule: ICSSTopLevelRule =>
        topLevelRule.getAsCSSString(writerSettings, 0) -> mediaRule.getSourceLocation.getFirstTokenBeginLineNumber
      }
    }
  }
}
