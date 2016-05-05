package com.themillhousegroup.witchhunt

import java.net.URL

import org.specs2.mutable.Specification

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.Duration
import com.themillhousegroup.witchhunt.test.GithubPagesHelper._

class WitchhuntSpec extends Specification {

  val timeout = Duration(5, "seconds")

  // These pages are hosted on Github pages
  // Checkout gh-pages to add/edit content, and push to make it live.

  def await[T](f: Future[T]): T = Await.result(f, timeout)

  def inspect(target: URL, options: WitchhuntOptions = WitchhuntOptions()) = await(Witchhunt.inspect(target, options))

  "Witchhunt" should {

    "Perform checking on a plain styleguide" in {

      val result = inspect(basicStyleguide)

      result must haveLength(117)
    }

    "Perform checking on a styleguide that refers to another identical page and find the same number of violations" in {

      val result = inspect(styleguideWithLink)

      result must haveLength(113) // This second page has a <button> which satisfies an additional 4 rules
    }

    "Respect the 'includeMediaRules' option" in {

      val result = inspect(basicStyleguide, WitchhuntOptions(includeMediaRules = true))

      result must haveLength(118) // By including media-queries, an extra rule becomes enforceable

      result.find(_.selector == "button.never-matched") must not beNone
    }

    "Respect the 'initialPageOnly' option" in {

      val fTestAllPages = Witchhunt.inspect(styleguideWithCircularLink, WitchhuntOptions(initialPageOnly = false))
      val fTestFirstPageOnly = Witchhunt.inspect(styleguideWithCircularLink, WitchhuntOptions(initialPageOnly = true))

      await(
        for {
          allPagesResult <- fTestAllPages
          firstPageOnlyResult <- fTestFirstPageOnly
        } yield {
          allPagesResult must haveLength(113) // By navigating to all Pages we find the the extra <button> and find 113 violations
          firstPageOnlyResult must haveLength(117) // By restricting ourselves to the first page, we don't find the button and have 4 more violations
        }
      )
    }
  }
}
