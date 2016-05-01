package com.themillhousegroup.witchhunt.util

/**
 *
 * Takes a Map[ A, Set[B] ]
 * and "inverts" it to a
 * Map[ B, Set[A] ]
 *
 * For example:
 * Map(  "George Clooney"  -> Set("Ocean's Eleven", "Solaris"),
 *       "Brad Pitt"       -> Set("Interview with the Vampire", "Ocean's Eleven")
 * )
 *
 * would be "inverted" to:
 *
 * Map(    "Ocean's Eleven"              -> Set("George Clooney", "Brad Pitt"),
 *         "Solaris"                     -> Set("George Clooney"),
 *         "Interview with the Vampire"  -> Set("Brad Pitt")
 * )
 *
 */
object MapInverter {

  def invert[A, B](map: Map[A, Set[B]]): Map[B, Set[A]] = {
    val flipped = map.toSeq.flatMap {
      case (a, bs) =>
        bs.map { b =>
          b -> a
        }
    }

    flipped.groupBy {
      case (b, a) =>
        b
    }.map {
      case (b, as) =>
        b -> as.map(_._2).toSet
    }
  }
}
