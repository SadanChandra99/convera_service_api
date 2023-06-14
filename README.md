# Getting Started

### Overview
The simple template project for the Convera java services based on SpringBoot + Maven with an example of data api service integration.

### Local run
- Clone the project
- Install the [parent pom](https://gitlab.com/convera_platform/convera-spring-boot-super-pom) to your local maven repository
```mvn clean install```
- Enable annotation processing for lombok in your IDE
- Run ```mvn clean compile``` to generate the api client for product data service. See the API client definition at [postgress-data-api-swagger-v1.json](src/main/resources/api/postgress-data-api-swagger-v1.json) 
- Run ProductServiceApplication, it will be running at port ```8081```, see [application.properties](src/main/resources/application.properties)
- Use [product-service-api-template.postman_collection.json](product-service-api-template.postman_collection.json) postman collection to run the requests
- Access Swagger UI by [localhost:8081/swagger-ui.html](localhost:8081/swagger-ui.html) from your browser
- Access ```swagger.json``` API definition by [localhost:8081/v3/api-docs](localhost:8081/v3/api-docs)

#### Prerequisites for the demo 
- Running [product data api service](https://gitlab.com/convera_platform/convera-spring-boot-api-templates/-/tree/main/data-api-template/postgress-data-api) at port ```8080```