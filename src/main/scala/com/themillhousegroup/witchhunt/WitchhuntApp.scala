package com.themillhousegroup.witchhunt

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object WitchhuntOptionsParsing {
  val optionPrefix = "--"

  val allOptions = Map[(String, String), WitchhuntOptions => WitchhuntOptions](
    "--includeMediaRules" -> "Include style rules within @media queries" -> (_.copy(includeMediaRules = true)),
    "--initialPageOnly" -> "Only test the given page, don't 'spider' any others" -> (_.copy(initialPageOnly = true))
  )

  val allOptionKeys = allOptions.keys.map(_._1).toSet

  def optionFor(s: String): Option[WitchhuntOptions => WitchhuntOptions] = allOptions.find(o => o._1._1 == s).map(_._2)

  val parser = new scopt.OptionParser[WitchhuntOptions]("witchhunt") {
    head("witchhunt", "0.x")
    opt[Boolean]('m', "include-media-rules") action { (x, c) =>
      c.copy(includeMediaRules = x)
    } text ("Include style rules within @media queries")

    opt[Boolean]('i', "initial-page-only") action { (x, c) =>
      c.copy(initialPageOnly = x)
    } text ("Only test the given page, don't 'spider' any others")
  }
}

object WitchhuntApp extends App {
  import WitchhuntOptionsParsing._

  val defaultTimeout = Duration(15, "seconds")

  val arguments = args.toSeq

  if (arguments.isEmpty) {
    showUsage
  } else {
    parseArguments
  }

  private def showUsage(): Unit = {
    System.err.println("Usage: [options] styleguide-url\n")
    System.err.println("Supported options:")
    allOptions.keys.foreach {
      case (o, d) =>
        System.err.println(s"${o.padTo(24, ' ')} $d")
    }
  }

  def parseArguments(): Unit = {
    val options = arguments.takeWhile(_.startsWith(optionPrefix)).foldLeft(WitchhuntOptions()) {
      case (wo, optionString) =>
        optionFor(optionString).fold {
          System.err.println(s"WARNING: Option $optionString unrecognised")
          wo
        } { f =>
          println(s"Adding option $optionString")
          f(wo)
        }
    }

    arguments.find(!_.startsWith(optionPrefix)).map { urlString =>
      println(s"Target URL: $urlString")
      start(options, urlString)
    }.getOrElse {
      System.err.println("No styleguide-url specified!\n")
      showUsage
    }
  }

  def start(options: WitchhuntOptions, urlString: String): Unit = {
    val url = new java.net.URL(urlString)

    val result = Witchhunt.inspect(url, options).map { violations =>
      violations.foreach(println)
      println(s"\n${violations.size} ${pluralize("violation", violations.size)} detected")
    }

    Await.result(result, defaultTimeout)
  }

  private def pluralize(word: String, count: Int): String = {
    if (count == 1) word else (word + 's')
  }
}
