package com.themillhousegroup.witchhunt

import java.net.URL

import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.themillhousegroup.witchhunt.test.GithubPagesHelper._

class WitchhuntSpec extends Specification {

  // These pages are hosted on Github pages
  // Checkout gh-pages to add/edit content, and push to make it live.

  def inspect(target: URL, options: WitchhuntOptions = WitchhuntOptions()) = Await.result(
    Witchhunt.inspect(target, options), Duration(5, "seconds")
  )

  "Witchhunt" should {

    "Perform checking on a plain styleguide" in {

      val result = inspect(basicStyleguide)

      println
      println(result.mkString("\n"))
      println

      result must haveLength(117)
    }

    "Perform checking on a styleguide that refers to another identical page and find the same number of violations" in {

      val result = inspect(styleguideWithLink)

      println
      println(result.mkString("\n"))
      println

      result must haveLength(113) // This second page has a <button> which satisfies an additional 4 rules
    }

    "Respect the 'includeMediaRules' option" in {

      val result = inspect(basicStyleguide, WitchhuntOptions(includeMediaRules = true))

      println
      println(result.mkString("\n"))
      println

      result must haveLength(119) // By including media-queries, an extra 2 rules become enforceable
    }
  }
}
