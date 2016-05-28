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

  private def toSelectorDeclarationsAndLineNumber(declarations: Seq[CSSDeclaration])(selector: CSSSelector): (String, Seq[CSSDeclaration], Int) = {
    (selector.getAsCSSString(writerSettings, 0),
      declarations,
      selector.getSourceLocation.getFirstTokenBeginLineNumber
    )
  }

  private def toSelectorDeclarationsAndLineNumberSeq(styleRule: CSSStyleRule): Seq[(String, Seq[CSSDeclaration], Int)] = {
    val selectors = styleRule.getAllSelectors.asScala.filter(queryable)
    val declarations = styleRule.getAllDeclarations.asScala.toSeq

    selectors.map(toSelectorDeclarationsAndLineNumber(declarations))
  }

  lazy val styleRules: Seq[(String, Seq[CSSDeclaration], Int)] = {
    val rules = stylesheet.getAllStyleRules.asScala

    rules.flatMap(toSelectorDeclarationsAndLineNumberSeq)
  }

  lazy val mediaRules: Seq[(String, Seq[CSSDeclaration], Int)] = {
    stylesheet.getAllMediaRules.asScala.flatMap { mediaRule: CSSMediaRule =>
      mediaRule.getAllRules.asScala.flatMap {
        _ match {
          case (styleRule @ (_: CSSStyleRule)) => toSelectorDeclarationsAndLineNumberSeq(styleRule)
          case _ => Nil
        }
      }
    }
  }
}
