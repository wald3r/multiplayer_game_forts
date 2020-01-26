A simple command line based multiplayer game. The game is basically some sort of clash of clans clone. Goal was to implement a stable network communication among the clients and the server. 

How to execute:

Start server first: java -jar Server.jar
Then you start a client: java -jar Client.jar ARG1 ARG2

ARG1: Port
ARG2: Can be whatever you want. It will activate, that „special settings“ get sent to the client regulary

Examples: java -jar Client.jar 4000 1 or java -jar Client.jar 4000
Note that the server port is 5000. Don‘t use the same.


Play with Rebels: java -jar Rebels.jar ARG1 ARG2

ARG1: Port
ARG2: Number of Rebels

Example: java -jar Rebels.jar 6000 20
Note that it will create 20 rebel clients starting from port 6000. The port number increases with every client.
