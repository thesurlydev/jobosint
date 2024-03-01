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

## TODO

- Infrastructure as Code
  - Terraform
- Containerization
- Comprehensive configuration
  - Support for environment variables to override
- CI/CD pipeline
  - GitHub Actions
- OpenTelemetry logs, traces and metrics
  - micrometer?
- Deployment paths to a VM and Kubernetes
- Documentation and OpenAPI specification
- Testing
  - Test containers
  - Playwright
- UI
  - SvelteKit?
  - Vue?
  - [iced](https://iced.rs/)?
