package prices.services

case object TooManyRequestsException extends Exception("Too many requests")

case object InvalidRequestExcption extends Exception("Invalid request")

case object GenericExcpetion extends Exception("Unknown error")
