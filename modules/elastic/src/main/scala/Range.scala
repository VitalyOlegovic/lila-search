package lila.search

import com.sksamuel.elastic4s.ElasticDsl.*

final class Range[A] private (val a: Option[A], val b: Option[A]):

  def queries(name: String) =
    a.fold(b.toList.map { bb => rangeQuery(name).lte(bb.toString) }) { aa =>
      b.fold(List(rangeQuery(name).gte(aa.toString))) { bb =>
        List(rangeQuery(name).gte(aa.toString).lte(bb.toString))
      }
    }

  def map[B](f: A => B) = new Range(a.map(f), b.map(f))

  def nonEmpty = a.nonEmpty || b.nonEmpty

object Range:

  def apply[A](a: Option[A], b: Option[A])(using o: Ordering[A]): Range[A] =
    (a, b) match
      case (Some(aa), Some(bb)) =>
        o.lt(aa, bb)
          .fold(
            new Range(a, b),
            new Range(b, a)
          )
      case (x, y) => new Range(x, y)

  def none[A]: Range[A] = new Range(None, None)
