# Network types for scala
This library adds types and operations for networking concepts so we can reason about those in a typesafe manner.

## Usage

```scala
import nl.codebulb.nettypes._

Ipv4Address("127.0.0.1")
// or
Ipv4Address.LOCAL_HOST

```
Will give you:
scala> res0: Option[nl.codebulb.nettypes.Ipv4Address] = Some("127.0.0.1")

Using this library you can ensure that when working with network types like IP Addresses, ports, etc, you really get those.
Every apply method ensures that the input is valid and returns a None otherwise.


Ipv4Address:
```scala
import nl.codebulb.nettypes._
import nl.codebulb.nettypes.implicits._

// create an ipv4 address
val ip = Ipv4Address("192.168.1.1")

// Staticly typed strings can be converted to ipv4 without the Option like so:
"192.168.1.1".ipv4 



```
Ipv6Address:
```scala
import nl.codebulb.nettypes._
import nl.codebulb.nettypes.implicits._

//From condensed string or uncondensed string
val ip6 = Ipv6Address("2001:DB8::8:800:200C:417A")
val anotherIp6 = Ipv6Address("2001:DB8:0:0:8:800:200C:417A")
val homev6 = Ipv6Address("::1")
val anotherHomev6 = Ipv6Address.LOCAL_HOST

//Or from static string:
"::1".ipv6
```

Port:
```scala
import nl.codebulb.nettypes._

val port = Port(80)
```

SocketAddress:
```scala
import nl.codebulb.nettypes._

val socket = SocketAddress(Ipv4Address("127.0.0.1"), Port(80))

val socket6 = SocketAddress(Ipv6Address("::1"), Port(80))

```

Constructing network types is easy and intuitive. Theres also some helpfull operations on available on some types

```scala
val ip = "192.168.1.1".ipv4
val next = ip.next // 192.168.1.2
val previous = next.previous // 192.168.1.1
    
def skip5(ip: Ipv4Address): Ipv4Address = {
  Ipv4Address.fromBigInt(ip.toBigInt+5).asInstanceOf[Ipv4Address]
}
    
ip.transform(skip5 _)
    
```

## Testing
run ``sbt test`` to run the tests. 
