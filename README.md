# 🎾 MatchApp

A backend REST API application for analyzing **UTR** and **USTA** tennis data, built with **Spring Boot** and **MySQL** (hosted on DigitalOcean).  
Designed to provide powerful endpoints for match analysis, player statistics, team insights, and more.

---

## 📖 Description

**MatchApp** serves as the backbone for tennis data analysis, focusing on:
- Processing and analyzing match data from UTR and USTA platforms.
- Providing RESTful APIs to fetch insights about players, teams, and match outcomes.
- Supporting tennis enthusiasts, captains, and analysts in making data-driven decisions.

Built with:
- **Spring Boot** for scalable, high-performance API services.
- **MySQL** (hosted on DigitalOcean) for efficient relational data management.

---

## 🏗️ Architecture

The application architecture follows a clean and modular structure, with clear separation of concerns between data access, service logic, and API endpoints.

![Match App Architecture](MatchApp.png "Match App Architecture")

**Key Components:**
- **Controller Layer:** Exposes RESTful endpoints for consumers.
- **Service Layer:** Contains core business logic for data processing and analysis.
- **Repository Layer:** Interfaces with the MySQL database to perform CRUD operations.
- **Database:** Structured schema to store player data, team info, match history, etc.

---

## 🗄️ Database Diagram

A well-structured relational database schema supports efficient querying and scalability.

![Match DB Diagram](Match.png "MatchApp Database Schema")

**Schema Highlights:**
- Player profiles with UTR and USTA ratings.
- Teams and team-member relationships.
- Match records, scores, and detailed statistics.
- Historical data to enable trend analysis.

---

## 🔐 Security Features

The application implements several security best practices:

- **UTR API Token Management:** Secure handling of UTR API authentication tokens using environment variables and externalized configuration. See [UTR API Security](UTR_API_SECURITY.md) for details.
- **Database Credentials:** Secure storage of database credentials.
- **Docker Security:** Non-root user execution in containerized deployments.

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL database (hosted on DigitalOcean or local)
- UTR API token (for UTR data access)

### Installation & Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/austinxyz/MatchApp.git
   cd MatchApp

2. **Configure the database connection:**
- Update application.yml or application.properties with your MySQL credentials.

3. **Configure the UTR API token:**
   - Option 1: Update the `utr.api.token` property in application.properties
   - Option 2: Set the `UTR_API_TOKEN` environment variable (recommended for production)
   - See [UTR API Security](UTR_API_SECURITY.md) for detailed instructions

4. **Build and run the application:**
    ```bash
   mvn clean install
   mvn spring-boot:run
    ```

5. **Access the application:**
   - API base URL: http://localhost:8080
   - Check token status: http://localhost:8080/api/utr/token/status

## 🐳 Docker Deployment

The application can be deployed using Docker for consistent environments:

1. **Build the Docker image:**
   ```bash
   docker build -t matchapp -f Dockerfile.secure .
   ```

2. **Run with environment variables:**
   ```bash
   docker run -p 8080:8080 -e UTR_API_TOKEN=your_token_here matchapp
   ```

3. **Using Docker Compose:**
   ```bash
   # Copy the example files
   cp docker-compose.env.example .env
   cp docker-compose.yml.example docker-compose.yml
   
   # Edit the .env file with your credentials
   nano .env
   
   # Start the application
   docker-compose up -d
   ```
