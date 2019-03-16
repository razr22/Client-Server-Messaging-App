# Client-Server-Messaging-App [![Build Status](https://travis-ci.com/razr22/BlockchainApp.svg?token=zyDw6qfXm2H66zXaBysi&branch=master)](https://travis-ci.com/razr22/BlockchainApp)

##### With the knowledge gained from socket programming in Java for the CDN application, this program extends the server functionality to allow for communication between two or potentially more parties through a host server node which registers and logs each users message into a block and onto a block list or chain of that conversations messages. This log is then distributed to all parties involved and the third party server. Concepts such as message encryption and hashing, reading/writing files, etc. are implemented.

###### Currently Debugging an issue with multiple user real time chatting sessions due to socket read/writing availability on one local machine.

    TO COMPILE (SERVER):
      javac -cp jars/\* HelperClasses/*.java Server/*.java
  
    TO RUN (SERVER):
      java -cp .:jars/\* Server/Server
