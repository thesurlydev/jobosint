# jobosint.com

Job open source intelligence (OSINT) and manager for jobseekers.

## Technology Stack

- Spring Boot
- Spring Data JDBC
- Java 21
- PostgreSQL

## Database

To connect to the database, use the following command:

```shell
PGPASSWORD=shane psql -h bleepboop -U shane -d jobosint
```

## Versions

This project uses the Gradle refreshVersions plugin to manage dependencies. 

To update the versions, run the following command:

```
./gradlew refreshVersions
```



## TODO

- Containerization
- CI/CD pipeline
- OpenTelemetry logs, traces and metrics
  - micrometer?
