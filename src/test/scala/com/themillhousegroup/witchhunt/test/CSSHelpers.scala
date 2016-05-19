package com.themillhousegroup.witchhunt.test

object CSSHelpers {
  val simpleStyleRuleBlock =
    """|html {
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
     """.stripMargin

  val simpleMediaRuleBlock =
    """|@media only screen and (max-device-width: 480px) {
      |  .radio-button {
      |    color: pink;
      |  }
      |}
      |
      |header span.green {
      |   color: green;
      |   background-color: green;
      |}
    """.stripMargin

  val repeatedRuleBlock =
    """|html {
      |   color: black;
      |   background-color: red;
      |}
      |
      |header {
      |   color: pink;
      |   background-color: blue;
      |}
      |header {
      |   color: red;
      |   background-color: green;
      |}
    """.stripMargin

  val redundantRuleBlock =
    """|
      |html header {
      |   color: pink;
      |   background-color: blue;
      |}
      |header {
      |   color: red;
      |   background-color: green;
      |}
    """.stripMargin
}
