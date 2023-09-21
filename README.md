# SpringBootTree

## Prerequisites
Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK) 17 installed.
- Maven build tool installed.
- [Postman](https://www.postman.com/downloads/) installed for API testing.

## Project Setup
1. Clone the repository:
```
git clone https://github.com/AdamAmirah/SpringBootTree.git
```
2- Move to SpringBootTree
```
cd SpringBootTree
```
3- Install Packages
```
mvn clean install
```
4- Run the Spring Boot application
```
mvn spring-boot:run
```

## API Testing

1. Use Postman to access the endpoint http://localhost:8080/api/v1/statements with a GET request
2. In the body tab Add your JSON that contains the params :
  ```
 {
    "accountId" : 3,
    "fromDate" : "18.11.2019" ,
    "toDate" : "15.11.2020",
    "fromAmount" : "87",
    "toAmount" : "761"
}
  ``` 
3- In the Authorization tab, add the username & Admin credentials 
  ```
  User1: Username: admin & Password: admin
  User2: Username: user & Password: user
  ``` 
4- Click Send. 

## Unit Testing
```
mvn test
```


## Notes
- The provided MS Access DB has records that date back to 2020. Therefore when testing the /statements endpoint with the User role, Please add sooner records or use the provided DB in this repository
- Printing SonarQube reports is a paid feature.
