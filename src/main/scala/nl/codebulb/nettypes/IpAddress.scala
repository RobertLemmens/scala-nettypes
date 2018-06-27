package nl.codebulb.nettypes

sealed trait IpAddress extends Serializable{

  protected val bytes: Array[Byte]

  def toBytes: Array[Byte] = bytes.clone()

  def toBigInt: BigInt = {
    var result = BigInt(0)
    bytes.foreach(b => result = (result << 8) | (0x0ff & b))
    result
  }

  def next: IpAddress

  def previous: IpAddress

  def transform[A <: IpAddress](a: A)(transformer: A => A): A

}

final class Ipv4Address(protected val bytes: Array[Byte]) extends IpAddress {

  override def next: IpAddress = Ipv4Address.fromBigInt(this.toBigInt+1)

  override def previous: IpAddress = Ipv4Address.fromBigInt(this.toBigInt-1)

  def transform(transformer: Ipv4Address => Ipv4Address): Ipv4Address = transform[Ipv4Address](this)(transformer)

  override def transform[A <: IpAddress](a: A)(transformer: A => A): A = transformer(a)

  override def toString: String =
    s"${bytes(0) & 0xff}.${bytes(1) & 0xff}.${bytes(2) & 0xff}.${bytes(3) & 0xff}"

}

object Ipv4Address {

  val LOCAL_HOST = Ipv4Address("127.0.0.1")

  def apply(address: String): Option[Ipv4Address] = {
    val fields = address.trim.split('.')
    if(fields.length  > 4)
      None
    else {
      val bytes = new Array[Byte](4)
      val f1 = fields(0).toInt
      val f2 = fields(1).toInt
      val f3 = fields(2).toInt
      val f4 = fields(3).toInt
      if(isInRange(f1) && isInRange(f2) && isInRange(f3) && isInRange(f4)) {
        bytes(0) = f1.toByte
        bytes(1) = f2.toByte
        bytes(2) = f3.toByte
        bytes(3) = f4.toByte
        Some(unsafeFromBytes(bytes))
      } else {
        None
      }
    }

  }

  private def unsafeFromBytes(bytes: Array[Byte]): Ipv4Address = new Ipv4Address(bytes)

  private def isInRange(value: Int): Boolean = {
    if(value >= 0 && value <= 255) true else false
  }

  def fromBigInt(value: BigInt): IpAddress = {
    val bytes = new Array[Byte](4)
    var rem = value
    for (i <- 3 to 0 by -1) {
      bytes(i) = (rem & 0x0ff).toByte
      rem = rem >> 8
    }
    unsafeFromBytes(bytes)
  }

}

final class Ipv6Address(protected val bytes: Array[Byte]) extends IpAddress {
  override def next: IpAddress = Ipv6Address.fromBigInt(this.toBigInt+1)

  override def previous: IpAddress = Ipv6Address.fromBigInt(this.toBigInt-1)

  def transform(transformer: Ipv6Address => Ipv6Address): Ipv6Address = transform[Ipv6Address](this)(transformer)

  override def transform[A <: IpAddress](a: A)(transformer: A => A): A = transformer(a)

  override def toString: String = {
    val builder = new StringBuilder
    val bytes = toBytes
    for(i<-0 until 15 by 2) {
      val field = ((bytes(i) & 0xff) << 8) | (bytes(i+1) & 0xff)
      val hextet = f"$field%04x"
      builder.append(hextet)
      if (i < 14) builder.append(":")
    }
    builder.toString
  }
}

object Ipv6Address {

  val LOCAL_HOST = Ipv6Address("::1")

  def apply(address: String): Option[Ipv6Address] = {
    val fields = address.trim.split(":")
    val bytes = new Array[Byte](16)

    val filledFields = if(fields.size < 8) {
      val missingStrings = 8 - fields.count(str => !str.equals(""))
      val stringBuilder = new StringBuilder
      for(i <- 0 until missingStrings) {
        stringBuilder.append("0000")
        stringBuilder.append(":")
      }
      val filledAddress = if(address.charAt(0) == ':')
        address.replace("::", stringBuilder.toString())
      else
        address.replace("::", ":"+stringBuilder.toString())

      filledAddress.trim.split(":")
    }
    else
      fields

    var fieldIncr = 0
    if(!filledFields.forall(isInRange))
      None
    else {
      for(i <- 0 until 15 by 2) {
          val fieldValue = Integer.parseInt(filledFields(fieldIncr), 16)
          bytes(i) = (fieldValue >> 8).toByte
          bytes(i+1) = fieldValue.toByte
          fieldIncr += 1
      }
      Some(unsafeFromBytes(bytes))
    }

  }

  private def unsafeFromBytes(bytes: Array[Byte]): Ipv6Address = new Ipv6Address(bytes)

  private def isInRange(field: String): Boolean = {
    if(Integer.parseInt(field, 16) > 0xffff && !(Integer.parseInt(field, 16) < 0)) false else true
  }

  def fromBigInt(value: BigInt): IpAddress = {
    val bytes = new Array[Byte](16)
    var rem = value
    for (i <- 15 to 0 by -1) {
      bytes(i) = (rem & 0x0ff).toByte
      rem = rem >> 8
    }
    unsafeFromBytes(bytes)
  }

}