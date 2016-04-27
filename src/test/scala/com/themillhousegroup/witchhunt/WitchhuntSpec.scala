package com.themillhousegroup.witchhunt

import java.net.URL

import org.specs2.mutable.Specification

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.themillhousegroup.witchhunt.test.GithubPagesHelper._

class WitchhuntSpec extends Specification {

  // These pages are hosted on Github pages
  // Checkout gh-pages to add/edit content, and push to make it live.

  def inspect(target: URL) = Await.result(
    Witchhunt.inspect(target), Duration(5, "seconds")
  )

  "Witchhunt" should {

    "Perform checking on a plain styleguide" in {

      val result = inspect(basicStyleguide)

      println
      println(result.mkString("\n"))
      println

      result must haveLength(117)
    }
  }
}
