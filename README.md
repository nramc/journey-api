![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/nramc/journey-api/ci-build-workflow.yml?branch=main&style=flat&logoColor=ff0)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=nramc_journey-api&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=nramc_journey-api)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=nramc_journey-api&metric=coverage)](https://sonarcloud.io/summary/new_code?id=nramc_journey-api)
[![Release](https://img.shields.io/github/release/nramc/journey-api.svg?style=for-the-badge?logoColor=fff&style=flat)](https://github.com/nramc/journey-api/releases)
[![Documentation](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=black&style=for-the-badge)](https://journey-api-nxm5.onrender.com/doc/swagger-ui.html)
[![Docker](https://img.shields.io/badge/Docker-2CA5E0?logo=docker&logoColor=white&style=flat)](https://hub.docker.com/r/codewithram/journey-api)
[![Badge](https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=159&style=flat)](https://www.linkedin.com/in/ramachandran-nellaiyappan/)

# Journey Rest API

<hr />

This **Java-based Backend For Frontend (BFF)** service is designed to seamlessly integrate with
the [Journeys](https://nramc.github.io/journeys/) Single Page
Application (SPA), offering robust backend support for frontend operations.

Built using the powerful [Spring Framework](https://spring.io/), this service handles complex request processing with a
focus on geographical data management. Key features include:

- Scalable Data Persistence: Leveraging [MongoDB Atlas](https://www.mongodb.com/products/platform/atlas-database), the
  service ensures reliable and scalable data storage.
- Geospatial Support: With geographical information at its core, the service
  utilizes [GeoJSON](https://datatracker.ietf.org/doc/html/rfc7946) data format, made simple
  through the [Commons GeoJson](https://github.com/nramc/commons) library, for accurate and efficient handling of
  location-based data.

## Helpful Links

- [Journeys Application](https://nramc.github.io/journeys/)
- [REST API Documentation](https://journey-api-nxm5.onrender.com/doc/swagger-ui.html)
- [REST Open API YML Configuration](https://journey-api-nxm5.onrender.com/doc/openapi)

## Getting Started

Please find below steps to setup and run application in your workstation.

### Prerequisites

The service uses MongoDB for persistence.
For Local development, Local MongoDB instance created with help of Docker CLI.
So make sure you have installed Docker CLI or any other docker container tool.

### Installation

1. Download Repository
   ```sh
   git clone https://github.com/nramc/journey-api.git 
   ```
2. Run Application with spring profile `dev`
   ```sh
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
3. Local MongoDB can be accessed in [http://localhost:9090/](http://localhost:9090/) with help
   of [Mongo Express](https://github.com/mongo-express/mongo-express)
4. To test REST resource,
   use [IntelJ Http Client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) scripts
   available in ``src/test/resources/http-scripts/`` directory.

### Contributing

Any contributions you make are **greatly appreciated**.

If you like the project and have a suggestion that would make this better, please fork the repo and create a pull
request.
You can also simply open an issue with the tag "enhancement".

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add the AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Release new version

1. Manually
   run [Prepare Release Workflow](https://github.com/nramc/journey-api/blob/main/.github/workflows/prepare-release.yml)
   with release version "MAJOR.MINOR.PATCH" e.g. 0.0.1
2. The Workflow flow creates a new tag with provided release version vMAJOR.MINOR.PATCH e.g. v0.0.1
3. The Workflow creates [Release](https://github.com/nramc/journey-api/releases) as well with release notes and make the
   release as latest
4. As soon as new `Release` created, which
   triggers [Release Workflow](https://github.com/nramc/journey-api/blob/main/.github/workflows/release-workflow.yml)
   with release event
5. The `Release` workflow build & test project after checkout
6. When build successful, creates Docker image and publish it to [Docker Hub](https://hub.docker.com/)
7. Finally triggers [Render](https://dashboard.render.com/) webhook for deployment

## Contact

Ramachandran
Nellaiyappan [Website](https://github.com/nramc) | [Twitter](https://twitter.com/ram_n_74) | [E-Mail](mailto:ramachandrannellai@gmail.com)

## Credits

Sincere Thanks to following open source community for their wonderful efforts to make our life much easier.

- [Spring IO](https://spring.io/) - Java Web Framework
- [MongoDB](https://www.mongodb.com/) - Persistence Layer
- [Spring Rest Doc](https://springdoc.org) - Spring REST Doc with Open API support for Swagger UI
- [OpenRewrite](https://docs.openrewrite.org/) - Automated source code refactoring
- [Docker](https://www.docker.com/) - Containerization
- [Testcontainers](https://testcontainers.com/) - Run containers on-demand for development and testing

## Show your support

Give a ⭐️ if you like this project!
