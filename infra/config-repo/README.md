# Configuration Repository

This folder is used by the Spring Cloud Config Server to serve configuration files for all microservices in the system.

Drop your configuration files here, such as:
- `api-gateway.yml`
- `catalog-service.yml`
- `loan-service.yml`

**Note:** When you push changes to files in this directory to GitHub, the Config Server will detect them. Make sure to configure your CI/CD pipeline to ignore changes in this folder so that modifying configurations does not trigger full code rebuilds.

**Note:** Only put downstream services configs here (services, gateway), not config for infrastructure (eureka, config-server) 

