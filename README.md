# CPE-S7_IOT_InfraProject
Projet "Infrastructure" - "Internet Of Things" - CPE Lyon (S7)


# Protocol

Protocol for the messages related to sensors data : temperature, brightness, humidity querying, data sending...

________________________________________________
## Object sending
- `T[value]` : temperature value sent by the object
- `B[value]` : brightness value sent by the object
- `H[value]` : humidity value sent by the object

## Gateway querying
### Gateway sensor request
- `QT` : temperature value queried by the gateway
- `QB` : brightness value queried  by the gateway
- `QH` : humidity value queried  by the gateway

###  Object answering gateway query
- `AT[value]` : answering temperature value queried by the gateway
- `AB[value]` : answering brigthness value queried by the gateway
- `AH[value]` : answering humidity value queried by the gateway
- `AET` : the temperature sensor can't measure the data
- `AEB` : the brightness sensor can't measure the data
- `AEH` : the humidity sensor can't measure the data
- `AEU` : the gateway's query has generated an unknown error
