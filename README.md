# Proto4j-XTral

This project contains a fully implemented client-server infrastructure controlled by Java-Annotations. A general 
instruction on how to use this framework and how the required classes should be implemented is provided below.

The communication can be built on single and/or multicast connections and the receiving and writing process must 
be declared. To use a client or server, the following classes must be implemented:

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

For more information about how to implement each component, please refer to the `proto4j-msdp` repository ([link](https://github.com/Proto4j/proto4j-msdp)). It can be used as an example for further developing.

Some Examples:

````java
XTralClient client = XTral.client(FooClient.class);
client.start(fooArg1, barArg2);
// same with a server
XTralServer server = XTral.server(FooServer.class, "argument1", "optional2");
server.start();
````

Both, client and server structures must provide the following packaging:

    root/
      MyClient.class
      Foo.class
      ...
      sub-package/
        Handler1.class
        Handler2.class
      ...

All classes that are stored in the root and sub-packages will be used within the client or server.

### Client

Usually a client is used to connect to a server and send messages to it. The following client just connects to the localhost:

````java
package src.localhost.client;

@Client // used to indicate that this class will be the base reference
@AllowConfig // indicates that this class provides the configuration object
public class LocalHostClient implements XTralConfigurationFactory<LocalHostClient> {
  
  @Client.Entry // this annotation allows the XTralClient to start on this method
  public void connectToServer(XTralClient client) {
    FooConnection connection = client.openConnection(); // open but do not connect
    
    connection.init(new MyConnectSpec()); // or null of no spec was implemented
    connection.doConnect(InetAddress.getLocalHost(), 4444); // connect to server
  }
  
  @Override
  public XTralConfiguration<MsdpClient> createConfiguration() {
    return new MyLocalHostConfiguration(); // implemented configuration
  }  
}
````

In order to react to messages that should be sent and messages that have been received, `Agent` classes have to be defined. They
consist of methods that should react as a handler:

````java
package src.localhost.client.handler; // note the sub-package here 

@Agent // this class will be a worker
public MessageListener {

  @InboundHandler(addFirst = true) //handle incoming messages first
  public void onMessage(Object msg, FooConnection connection) {
    // we assume the msg is a string
    System.out.println((String)msg);
  }
  
  // last handler that can modify the message
  @OutboundHandler(addLast = true) 
  public String onMessagePrepared(String msg) {
    // the returned object will be sent to the server
    if (msg == null) return "no-message";
    else return msg; 
  }
}
````

There are three pre-defined handler annotations: `InboundHanler`, `OutboundHandler` and `ExceptionHandler`. It is also possible to declare own handlers that react to specific events in custom implementations of the `Connection` class:

````java
// this annotation marks this class as a handler annotation
@IncludedHandler 
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface FooHandler {}
````
## Download

Download the [latest JAR file](https://github.com/Proto4j/proto4j-xtral/releases) from the releases tab. This framework requires a minimum of Java 8+ for developing and running. 

## License

    MIT License
    
    Copyright (c) 2023 Proto4j-Group
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

