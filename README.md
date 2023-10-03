# jobosint.com

Job open source intelligence and manager for jobseekers.

## Technology Stack

* Spring Boot
* Spring Data JDBC
* Java 17
* PostgreSQL
* Flyway


## Database

```bash
./rebuild-db.sh
```


## TODO

* HTTP client
* Integrate with:
  * https://serpapi.com
* Infrastructure as Code
  * Terraform
* Containerization
* Comprehensive configuration
  * Support for environment variables to override
* CI/CD pipeline
  * GitHub Actions
* OpenTelemetry logs, traces and metrics
  * micrometer?
* Deployment paths to a VM and Kubernetes
* Documentation and OpenAPI specification
* Testing
  * Test containers
  * Playwright
* UI
  * SvelteKit?
  * Vue?
  * [iced](https://iced.rs/)?

## References



## Toyota Parts Deal

### Part search

```
curl "https://www.toyotapartsdeal.com/api/search/search-words?searchText=90368-49084-77&isConflict=false" -H "Site: TPD"
```

Response:

```json
{
  "url": null,
  "code": 200,
  "data": {
    "isConfirmShow": false,
    "redirectUrl": "/oem/toyota~bearing~rtp~50~82~k~90368-49084-77.html",
    "make": null,
    "fakeMake": null,
    "model": null,
    "year": 0,
    "submodel": null,
    "extra1": null,
    "extra2": null,
    "vin": null,
    "vehicles": null
  },
  "message": null
}
```
