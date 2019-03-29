# Text-based protocol

In this project, communication between client and server is based on text-based protocol and on TCP.
Client app provides us Command Line Interface where we can make some calculations.

Below we can see example communication session.

![alt text](https://raw.githubusercontent.com/DavidSolomon22/Text-based_protocol/master/12.jpg)

Marked in blue - packets sent by server.
Marked in red - packets sent by client.

Description of a packet:
  - O - operation field,
  - S - status field,
  - I - session ID field,
  - T - time stamp field,
  - K - first number field,
  - L - second number field.
