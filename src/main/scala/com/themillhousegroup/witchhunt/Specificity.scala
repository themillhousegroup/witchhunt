package com.themillhousegroup.witchhunt

import scala.collection.generic.CanBuildFrom
import scala.util.matching.Regex

/**
 * A Scala-fication of:
 * https://github.com/keeganstreet/specificity/blob/master/specificity.js
 */

case class SpecificityResult(selector:String, id:Int, classAttributePseudo:Int, elemPseudo:Int) {
  /** https://stuffandnonsense.co.uk/archives/css_specificity_wars.html */
  lazy val asInt:Int = (id*100) + (classAttributePseudo*10) + elemPseudo
}

object Specificity {

  val attributeRegex = """(\[[^\]]+\])""".r
  val idRegex = """(#[^\s\+>~\.\[:]+)""".r
  val classRegex = """(\.[^\s\+>~\.\[:]+)""".r
  val pseudoElementRegex = """(::[^\s\+>~\.\[:]+|:first-line|:first-letter|:before|:after)""".r
  // A regex for pseudo classes with brackets - :nth-child(), :nth-last-child(), :nth-of-type(), :nth-last-type(), :lang()
  val pseudoClassWithBracketsRegex = """(:[\w-]+\([^\)]*\))""".r
  // A regex for other pseudo classes, which don't have brackets
  val pseudoClassRegex = """(:[^\s\+>~\.\[:]+)""".r
  val elementRegex = """([^\s\+>~\.\[:]+)""".r

  def incrementClassA(sr:SpecificityResult, amount:Int):SpecificityResult = sr.copy(id = sr.id+amount)
  def incrementClassB(sr:SpecificityResult, amount:Int):SpecificityResult = sr.copy(classAttributePseudo = sr.classAttributePseudo+amount)
  def incrementClassC(sr:SpecificityResult, amount:Int):SpecificityResult = sr.copy(elemPseudo = sr.elemPseudo+amount)

  def calculate(selectors:String):Seq[SpecificityResult] = {
    selectors.split(",").map(calculateSingle)
  }

  def calculate[T, I[T] <: Iterable[T]](selectors:I[String])(implicit bf: CanBuildFrom[Iterable[String], SpecificityResult, I[SpecificityResult]]):I[SpecificityResult] = {
    selectors.map(calculateSingle)
  }

  def calculateSingle(selector:String):SpecificityResult = {

    // Using the same "remove matches" logic as the Javascript, but without the mutation...
    val ops = Seq[(SpecificityResult, String) => (SpecificityResult, String)](
      findMatch(attributeRegex, incrementClassB),
      findMatch(idRegex, incrementClassA),
      findMatch(classRegex, incrementClassB),
      findMatch(pseudoElementRegex, incrementClassC),
      findMatch(pseudoClassWithBracketsRegex, incrementClassB),
      findMatch(pseudoClassRegex, incrementClassB),
      findMatch(elementRegex, incrementClassC)
    )

    ops.foldLeft(SpecificityResult(selector, 0, 0, 0) -> selector) { case ((res, sel), func) =>
      func(res, sel)
    }._1
  }

  private def findMatch(regex:Regex, resultModifier:(SpecificityResult, Int) => SpecificityResult)(sr:SpecificityResult, selector:String):(SpecificityResult, String) = {
    regex.findFirstIn(selector).fold(sr -> selector) { _ =>
      val updatedSR = resultModifier(sr, regex.findAllMatchIn(selector).size)

      val updatedSelector = regex.findAllMatchIn(selector).foldLeft(selector) { (newSelector, m) =>
        val whiteout = m.matched.map(_ => ' ')
        val x = newSelector.replace(m.matched, whiteout)
        x
      }

      updatedSR -> updatedSelector
    }
  }
}
