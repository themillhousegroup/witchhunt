package com.themillhousegroup.witchhunt.test

import java.net.URL

object GithubPagesHelper {

  // These pages are hosted on Github pages
  // Checkout gh-pages to add/edit content, and push to make it live.

  val basicStyleguide = new URL("http://themillhousegroup.github.io/witchhunt/index.html")
  val styleguideWithLink = new URL("http://themillhousegroup.github.io/witchhunt/page-with-local-links.html")
  val styleguideWithCircularLink = new URL("http://themillhousegroup.github.io/witchhunt/page-with-multiple-local-links.html")

}
