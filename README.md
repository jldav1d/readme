## readme
A simple book catalogue web application made with **_Spring Boot_** as the backend and basic HTML/CSS/JS for the frontend. The webpage includes simple CRUD operations and user authentication to showcase Spring Boot's capabilities.

### Dependencies:
    - Spring Boot: enables fast development of web application
    - Spring Security: provides authentication tools to manage user authenticaiton
    - JPA/Hibernate: for data access and models
    - Spring-Test: provides mock objects for unit tests
    - Lombok: for helpful annotations to reduce code legibility

### Setup
1. Get a copy of this project, by cloning the repository
2. Resolve any dependency issues by fetching to the Maven Repositories
3. Start the MySQL docker container by running:
   `docker compose up`
4. Run the project with `mvn spring-boot: run`