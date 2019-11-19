# CPE-S7_IOT_InfraProject
Projet "Infrastructure" - "Internet Of Things" - CPE Lyon (S7)


# Protocol

Protocol for the messages related to sensors data : temperature, brightness, humidity querying, data sending...

________________________________________________
## Object sending
- `T[value]` : temperature value sent by the object
- `L[value]` : luminosity value sent by the object
- `H[value]` : humidity value sent by the object

## Gateway querying
### Gateway sensor request
- `QT` : temperature value queried by the gateway
- `QL` : luminosity value queried  by the gateway
- `QH` : humidity value queried  by the gateway
- `C[sensor order]` : config of the sensor value order displayed on the LCD screen [default order is TLH]
- `M[message]` : free message sent by the gateway to the object

###  Object answering gateway query
- `AT[value]` : answering temperature value queried by the gateway
- `AL[value]` : answering luminosity value queried by the gateway
- `AH[value]` : answering humidity value queried by the gateway
- `AET` : the temperature sensor can't measure the data
- `AEL` : the luminosity sensor can't measure the data
- `AEH` : the humidity sensor can't measure the data
- `AEU` : the gateway's query has generated an unknown error
