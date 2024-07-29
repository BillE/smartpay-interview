# Smartcloud instance prices API

This project provides a GET API to retrieve real-time machine instance prices from Smartcloud, a cloud instance provider. 

## Setup

As we are consuming data from a REST API, the configuration files must be set to the correct values. In the application.conf 
file we configure a URI for the REST API server as well as an auth token to validate requests. Both values are 
configured in the application.conf file and by default are set to 
run against the docker image at [smartcloud](https://hub.docker.com/r/smartpayco/smartcloud) running locally on port 9999. 

## Instructions

To start the service, simply run the following command. This will start the server which will then accept requests on port 8080.
```
sbt run
```

Two types of requests may be made
1. Price information for a given instance kind.
1. An exhaustive list of all instance kinds.

For price information, issue a request in the following format:

```
curl http://localhost:8080/prices?kind=sc2-small

```

For a list of all instance kinds, run:

```
curl http://localhost:8080/instance-kinds

```

## Assumptions
The code does not validate configuration and relies on the validity of the implementor. It is also assumed that the
consumer of the service will handle error codes correctly (400,500,etc.) and take appropriate action. 

## Design Decisions
GET requests for instance kind only accept a single value. Future implementations may accept a list of types to reduce
the number of calls the client must make to retrieve pertinent information. There is also no retry logic. If an upstream 
error occurs, the end user is prompted to try again. For error handling, we are specifically handling two use cases: 
1. usage quota exceeded (429)
1. Instance type not found (404)

All other errors are handled generically.
