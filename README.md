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

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- MySQL database (hosted on DigitalOcean or local)

### Installation & Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/austinxyz/MatchApp.git
   cd MatchApp

2. **Configure the database connection:**
- Update application.yml or application.properties with your MySQL credentials.

3. **Build and run the application:**
    ```bash
   mvn clean install
   mvn spring-boot:run

4. **Access the application:**
- API base URL: http://localhost:8080