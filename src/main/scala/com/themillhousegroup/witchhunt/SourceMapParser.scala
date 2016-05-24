package com.themillhousegroup.witchhunt

import java.io.File
import java.util.Base64

import scala.io.Source

import play.api.libs.json._

object Base64SingleCharacterDecoder {

  val bigA = 65
  // 'A'
  val bigZ = 90 // 'Z'

  var littleA = 97
  // 'a'
  val littleZ = 122 // 'z'

  val zero = 48
  // '0'
  val nine = 57 // '9'

  val plus = 43
  // '+'
  val slash = 47 // '/'

  val littleOffset = 26
  val numberOffset = 52

  /**
   * Decode a single base 64 character code digit to an integer. Returns -1 on
   * failure.
   */
  def decode(charCode: Int): Int = {

    if (bigA <= charCode && charCode <= bigZ) {
      // 0 - 25: ABCDEFGHIJKLMNOPQRSTUVWXYZ
      (charCode - bigA)
    } else if (littleA <= charCode && charCode <= littleZ) {
      // 26 - 51: abcdefghijklmnopqrstuvwxy
      (charCode - littleA + littleOffset)
    } else if (zero <= charCode && charCode <= nine) {
      // 52 - 61: 0123456789
      (charCode - zero + numberOffset)
    } else if (charCode == plus) {
      // 62: +
      62
    } else if (charCode == slash) { // 63: /  {
      return 63
    } else

      // Invalid base64 digit.
      return -1
  }
}

//https://github.com/mozilla/source-map/blob/master/lib/base64-vlq.js
object Base64VLQDecoder {

  val VLQ_BASE_SHIFT = 5

  // binary: 100000
  val VLQ_BASE = 1 << VLQ_BASE_SHIFT

  // binary: 011111
  val VLQ_BASE_MASK = VLQ_BASE - 1

  // binary: 100000
  val VLQ_CONTINUATION_BIT = VLQ_BASE

  // Keep decoding until there is no more
  def decodeBase64VLQ(vlq: String): Seq[Int] = {
    decodeBase64VLQFrom(vlq, 0)
  }

  def decodeBase64VLQFrom(aStr: String, aIndex: Int): Seq[Int] = {
    var strLen = aStr.length
    var result = 0
    var shift = 0
    var continuation = true
    var digit = 0
    var i = aIndex

    val returnValue = scala.collection.mutable.Seq()

    while (continuation) {

      if (aIndex >= strLen) {
        throw new IllegalArgumentException("Expected more digits in base 64 VLQ value.");
      }

      digit = Base64SingleCharacterDecoder.decode(aStr.charAt(i))
      i = i + 1
      if (digit == -1) {
        throw new IllegalStateException("Invalid base64 digit: " + aStr.charAt(i - 1));
      }

      continuation = (digit & VLQ_CONTINUATION_BIT) == 1
      digit &= VLQ_BASE_MASK
      result = result + (digit << shift)
      shift += VLQ_BASE_SHIFT

      println(s"Looping on $i - $digit - $result - $shift - continue: $continuation")
      returnValue :+ fromVLQSigned(result)
    }

    returnValue.toSeq
  }

  private def fromVLQSigned(aValue: Int): Int = {
    val isNegative = (aValue & 1) == 1
    val shifted = aValue >> 1
    if (isNegative) {
      -shifted
    } else {
      shifted
    }
  }
}

object SourceMapParser {

  def parse(mapFile: File): Boolean = {

    val content = Source.fromFile(mapFile).mkString

    val contentJson = Json.parse(content)

    println(contentJson)

    val outputFile = contentJson \\ "file"

    val inputFiles = contentJson \\ "sources"

    val mappings = (contentJson \\ "mappings").head.as[String]

    parseMappings(mappings)
    true
  }

  // Parse the mappings as per:
  // https://docs.google.com/document/d/1U1RGAehQwRypUTovF1KRlpiOFze0b-_2gc6fAH0KY0k/edit#
  private def parseMappings(mappings: String) = {
    val lines = mappings.split(";")
    lines.zipWithIndex.map {
      case (group, idx) =>
        println(s"Group: $idx")
        val segments = group.split(",")
        segments.map(parseSegment)
    }
  }

  private def parseSegment(segment: String) = {

    segment.length match {
      case 0 => println("")
      case 1 => println("1-length")
      case 4 => decodeFourFields(segment)
      case 5 => println("5-length")
    }
    println(s"$segment ")
  }

  private def decodeFourFields(segment: String) = {
    val fields = Base64VLQDecoder.decodeBase64VLQ(segment)
    println(s"4-length; fields: ${fields.length}")

  }
}
