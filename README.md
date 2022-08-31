# Small grpc api
This repo contains an example api using:
* Kotlin
* Maven
* Spring boot
* gRPC
* H2 Database
* JUnit 5

## How to run it
```$ssh
mvn spring-boot:run
```

* Access db - http://localhost:8080/h2-console
* Access grpc using grpcurl:
```$ssh
grpcurl -plaintext localhost:6565 list
```
```$ssh
grpcurl -plaintext localhost:6565 describe com.example.smallgrpcapi.PersonService
```
```$ssh
grpcurl -d '{"id": 1}' -plaintext localhost:6565 com.example.smallgrpcapi.PersonService/findPerson
```
