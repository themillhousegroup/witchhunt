package com.themillhousegroup.witchhunt

import java.net.URL

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object WitchhuntOptionsParsing {

  val parser = new scopt.OptionParser[WitchhuntOptions]("witchhunt") {
    head("witchhunt", "0.x")
    opt[Unit]('m', "include-media-rules") action { (_, c) =>
      c.copy(includeMediaRules = true)
    } text ("Include style rules within @media queries")

    opt[Unit]('p', "initial-page-only") action { (_, c) =>
      c.copy(initialPageOnly = true)
    } text ("Only test the given page, don't 'spider' any others")

    opt[Seq[String]]('i', "ignore-sheet-names") action { (names, c) =>
      c.copy(ignoreSheetNames = names)
    } valueName ("<name1>,<name2>...") text ("Exclude stylesheets with the given name(s)")

    arg[String]("<initial-url>") action { (url, c) =>
      c.copy(initialUrl = url)
    } text ("The URL to start inspecting")
  }
}

object WitchhuntApp extends App {
  import WitchhuntOptionsParsing._

  val defaultTimeout = Duration(15, "seconds")

  private def pluralize(word: String, count: Int): String = {
    if (count == 1) word else (word + 's')
  }

  private def start(options: WitchhuntOptions, url: URL): Unit = {

    val result = Witchhunt.inspect(url, options).map { violations =>
      violations.foreach(println)
      println(s"\n${violations.size} ${pluralize("violation", violations.size)} detected")
    }

    Await.result(result, defaultTimeout)
  }

  // Scopt will return a None if the options/args were unusable.
  parser.parse(args, WitchhuntOptions()).foreach { options =>
    start(options, new java.net.URL(options.initialUrl))
  }

}
