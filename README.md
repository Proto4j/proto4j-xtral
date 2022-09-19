# Proto4j-XTral

This project contains a fully implemented client-server infrastructure controlled by Java-Annotations. The communication
can be built on single and/or multicast connections and the receiving and writing process must be declared. To use a
client or server, the following classes must be implemented:

| Class                  | Client | Server | Multicast |
|------------------------|:------:|:------:|:---------:|
| XTralConfiguration     |   X    |   X    |     X     |  
| SocketFactory          |   X    |        |           |
| ChannelFactory         |   X    |   X    |     X     |
| ConnectionFactory      |   X    |   X    |     X     |
| ServerSocketFactory    |        |   X    |           |
| ServerBootstrapFactory |        |   X    |           |
| DatagramSocketFactory  |        |        |     X     |

The `Channel`, `Connection` and `ServerBootstrap` classes have to be implemented as well if they should be used. For these 
classes exists pre-defined abstract base classes that provide the basic behaviour.

## Basic Usage

