# Journey Rest API



##### Release new version:
1. Manually run [Prepare Release Workflow](https://github.com/nramc/journey-api/blob/main/.github/workflows/prepare-release.yml) with release version "MAJOR.MINOR.PATCH" e.g. 0.0.1 
2. The Workflow flow creates a new tag with provided release version vMAJOR.MINOR.PATCH e.g. v0.0.1
3. The Workflow creates [Release](https://github.com/nramc/journey-api/releases) as well with release notes and make the release as latest
4. As soon Release created triggers  [Release Workflow](https://github.com/nramc/journey-api/blob/main/.github/workflows/release-workflow.yml) with release event
5. The Release workflow build & test project after checkout
6. When build successful, creates Docker image and publish it to [Docker Hub](https://hub.docker.com/)
7. Finally triggers [Render](https://dashboard.render.com/) webhook for deployment



