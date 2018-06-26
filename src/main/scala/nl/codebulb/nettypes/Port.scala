package nl.codebulb.nettypes

final class Port(value: Int) extends Serializable {

  def copy(value: Int): Option[Port] = Port(value)
  override def toString: String = value.toString

}

object Port {

  val rangeMin: Int = 0
  val rangeMax: Int = 65535

  def apply(value: Int): Option[Port] = {
    if(value >= rangeMin && value <= rangeMax) Some(new Port(value)) else None
  }

}