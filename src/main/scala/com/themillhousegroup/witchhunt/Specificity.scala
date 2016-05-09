package com.themillhousegroup.witchhunt

import scala.collection.generic.CanBuildFrom

/**
 * A Scala-fication of:
 * https://github.com/keeganstreet/specificity/blob/master/specificity.js
 */

case class SpecificityResult(selector:String, id:Int, classAttribute:Int, pseudo:Int)

object Specificity {

  def calculate(selectors:String):Seq[SpecificityResult] = {
    selectors.split(",").map(calculateSingle)
  }

  def calculate[T, I[T] <: Iterable[T]](selectors:I[String])(implicit bf: CanBuildFrom[Iterable[String], SpecificityResult, I[SpecificityResult]]):I[SpecificityResult] = {
    selectors.map(calculateSingle)
  }

  def calculateSingle(selector:String):SpecificityResult = {
    SpecificityResult(selector, 0, 0, 0)
  }
}
