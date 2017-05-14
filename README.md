# keyvalue-service
 This is an key/value store that can gate changes to values on calls to external services.
 The keys and values are both simple strings, and the calls to external services are all standard HTTP calls.
 The application should return a response to every call in 5 seconds or less - regardless of how many external calls it must make. 
 
