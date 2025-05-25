# Currency Exchange Rates Application

A Spring Boot application that manages currencies and fetches exchange rates from https://openexchangerates.org

## Features
- Manage currencies (add, list)
- Fetch and store exchange rates on a schedule
- Get current exchange rates for any supported currency

## Prerequisites
- Java 17
- Docker and Docker Compose
- Maven

## Getting Started

1. Clone the repository:
```
git clone https://github.com/vladyshrytsenko/currency-exchange.git
cd currency-exchange
```

2. Set up environment:
Set your OpenExchangeRates API key in application.properties:
```
exchange-rates.api-key=API_KEY
```

3. Start PostgreSQL with Docker:
```
docker-compose up -d
```

4. Build and run the application:
```
mvn clean install
mvn spring-boot:run
```

5. API Docs:
http://localhost:8080/swagger-ui.html
