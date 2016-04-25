package com.themillhousegroup.witchhunt.test

object CSSHelpers {
  val simpleStyleRuleBlock =
    """
       |html {
       |   color: black;
       |   background-color: red;
       |}
       |
       |header {
       |   color: pink;
       |   background-color: blue;
       |}
       |
       |header span.green {
       |   color: green;
       |   background-color: green;
       |}
       |
     """.stripMargin
}
