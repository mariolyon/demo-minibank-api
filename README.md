# minibank
A rest-api server, that can transfer money between accounts

##Author
Mario Lyon
 
##Implementation
Java 11, and akka-http

##Installation
Install gradle and Jdk 11.

## Run
gradle installDist -x test
./build/install/minibank/bin/minibank

## To run the tests
gradle test

## to test the running server
```bash
#list accounts:
curl http://localhost:8080/accounts

#create account1:
curl -X POST 'http://localhost:8080/accounts'

#create account2:
curl -X POST 'http://localhost:8080/accounts'

#deposit money into account1
curl -X POST 'http://localhost:8080/accounts/1/deposit?amount=20'

#transfer money from account1 to account2 
curl -X POST 'http://localhost:8080/accounts/1/transfer?recipient=2&amount=20'
```
