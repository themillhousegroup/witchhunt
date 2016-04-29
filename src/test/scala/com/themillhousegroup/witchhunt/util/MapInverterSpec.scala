package com.themillhousegroup.witchhunt.util

import org.specs2.mutable.Specification

case class Actor(name: String)

class MapInverterSpec extends Specification {

  val tom = Actor("Tom Cruise")
  val george = Actor("George Clooney")
  val brad = Actor("Brad Pitt")

  "MapInverter" should {

    "Work on empty Maps" in {
      val m = Map[Actor, Set[String]]()

      MapInverter.invert(m) must beEmpty
    }

    "Work on a Map with one singleton set" in {
      val m = Map(tom -> Set("Mission Impossible"))

      MapInverter.invert(m) must beEqualTo(Map("Mission Impossible" -> Set(tom)))
    }

    "Work on a Map with one set" in {
      val m = Map(tom -> Set("Mission Impossible", "Mission Impossible 2"))

      MapInverter.invert(m) must beEqualTo(
        Map(
          "Mission Impossible" -> Set(tom),
          "Mission Impossible 2" -> Set(tom)
        ))
    }

    "Work on a Map with multiple distinct sets" in {
      val m = Map(
        tom -> Set("Mission Impossible", "Mission Impossible 2"),
        george -> Set("Ocean's Eleven", "Solaris")
      )

      MapInverter.invert(m) must beEqualTo(
        Map(
          "Mission Impossible" -> Set(tom),
          "Mission Impossible 2" -> Set(tom),
          "Ocean's Eleven" -> Set(george),
          "Solaris" -> Set(george)
        ))
    }

    "Work on a Map with multiple overlapping sets" in {
      val m = Map(
        tom -> Set("Mission Impossible", "Mission Impossible 2", "Interview with the Vampire"),
        george -> Set("Ocean's Eleven", "Solaris"),
        brad -> Set("Interview with the Vampire", "Ocean's Eleven")
      )

      MapInverter.invert(m) must beEqualTo(
        Map(
          "Mission Impossible" -> Set(tom),
          "Mission Impossible 2" -> Set(tom),
          "Ocean's Eleven" -> Set(george, brad),
          "Solaris" -> Set(george),
          "Interview with the Vampire" -> Set(tom, brad)
        ))
    }
  }
}
