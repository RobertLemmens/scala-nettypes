package nl.codebulb.nettypes

final case class SocketAddress(ip: IpAddress, port: Port) {

  override def toString: String =  ip match {
    case _:Ipv4Address => s"$ip:$port"
    case _:Ipv6Address => s"[$ip]:$port"
  }

}
