package nl.codebulb.implicits.ip

import nl.codebulb.nettypes.{Ipv4Address, Ipv6Address}

trait IpImplicits {

  implicit class IpAddressImplicits(address: String) {
    def ipv4: Ipv4Address = Ipv4Address(address).get
    def ipv6: Ipv6Address = Ipv6Address(address).get
  }

}
