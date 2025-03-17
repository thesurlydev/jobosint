# jobosint.com

Job open source intelligence (OSINT) and manager for job seekers.

## Technology Stack

- Spring AI
- Spring Boot
- Spring Data JDBC
- Java 21
- PostgreSQL

## Database

To connect to the database, use the following command:

```shell
PGPASSWORD=shane psql -h localhost -U shane -d jobosint
```

## Versions

This project uses the Gradle refreshVersions plugin to manage dependencies.

To update the versions, run the following command:

```
./gradlew refreshVersions
```

## TODO

- Add support for additional job boards:
  - smartrecruiters
- Containerization
- CI/CD pipeline
- OpenTelemetry logs, traces and metrics
    - micrometer?
