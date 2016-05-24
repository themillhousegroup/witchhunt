package com.themillhousegroup.witchhunt

import java.io.File

import org.specs2.mutable.Specification

class SourceMapParserSpec extends Specification {

  "SourceMap parser" should {

    "be able to parse a simple sourcemap" in {
      val f = new File("src/test/resources/main.css.map")
      SourceMapParser.parse(f) must beTrue

    }
  }
}
