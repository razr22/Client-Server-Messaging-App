# Client-Server-Messaging-App [![Build Status](https://travis-ci.org/razr22/Client-Server-Messaging-App.svg?branch=master)](https://travis-ci.org/razr22/Client-Server-Messaging-App)

##### With knowledge gained from socket programming in Java, this program extends server functionality to allow for communication between two or more parties through a host server node which registers and logs each users message into a block for that conversation. This log is then distributed to all parties involved. Concepts such as message encryption and hashing, reading/writing files, etc. are implemented.

##### --- To-Do ---
###### Debugging an issue with multiple user sessions.
###### Debugging consensus mechanism and log tampering. 
###### Move away from single server verification and distribution to P2P.
    TO COMPILE (SERVER):
      javac -cp jars/\* HelperClasses/*.java Server/*.java
  
    TO RUN (SERVER):
      java -cp .:jars/\* Server/Server
