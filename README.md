# FoodVoting

A Spring Boot web application for voting on food items. Users can browse dishes, cast ratings, and view ranked results.

**Live Demo:**(https://food-voting-vg87.onrender.com/)https://food-voting-vg87.onrender.com/)

## Features

- Browse food items with images and descriptions
- Vote with 1-5 star ratings and optional comments
- View ranked results with average ratings
- Add and delete food items
- REST API for programmatic access
- In-memory H2 database (zero setup, auto-seeded with 5 dishes)

## Tech Stack

- Java 21, Spring Boot 3.4, Gradle
- Spring MVC (Thymeleaf) + REST controllers
- Spring Data JPA (H2 database)
- Lombok, Jakarta Validation
- JUnit 5 + Mockito tests
- Docker (multi-stage build)

## Quick Start

**Locally:**
```bash
./gradlew bootRun
```

Open `http://localhost:8080`

H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:foodvoting`)

**Docker:**
```bash
docker build -t foodvoting .
docker run -p 8080:8080 foodvoting
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/food-items` | List all food items |
| GET | `/api/food-items/{id}` | Get food item by ID |
| POST | `/api/food-items` | Create food item |
| DELETE | `/api/food-items/{id}` | Delete food item |
| POST | `/api/votes` | Cast a vote |
| GET | `/api/votes/results` | Get ranked results |
| GET | `/api/votes/food-item/{id}` | Get votes for a food item |

## Build & Test

```bash
./gradlew build      # build + test
./gradlew test       # run tests only
```

## Deploy to Render

This project includes a `render.yaml` and `Dockerfile` for Render deployment.

1. Push to GitHub
2. In Render Dashboard, create a new **Web Service** connected to your repo
3. Set **Runtime** to **Docker**
4. Deploy

Or use Blueprint: connect your repo and Render auto-detects `render.yaml`.
