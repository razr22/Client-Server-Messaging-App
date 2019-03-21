# Client-Server-Messaging-App [![Build Status](https://travis-ci.org/razr22/Client-Server-Messaging-App.svg?branch=master)](https://travis-ci.org/razr22/Client-Server-Messaging-App)

##### With knowledge gained from socket programming in Java, this program extends server functionality to allow for communication between two or more parties through a host server node which registers and logs each users message onto a blockchain for that conversation. This log is then distributed to all parties involved and the third party server. Concepts such as message encryption and hashing, reading/writing files, etc. are implemented.

###### Currently Debugging an issue with multiple user sessions.
    TO COMPILE (SERVER):
      javac -cp jars/\* HelperClasses/*.java Server/*.java
  
    TO RUN (SERVER):
      java -cp .:jars/\* Server/Server
