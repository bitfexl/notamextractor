# Notam Extractor

This app extracts notams for a given ICAO location or for a given country from the DINS (https://www.notams.faa.gov/dinsQueryWeb/).

The notams get parsed and converted to java classes and then to json.

## Generate typescript interfaces

Run:

````
mvn process-classes
````

to generate typescript interfaces for the notam data class.

## GH Actions

**build-and-export.yml**:

This action is based on

- https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-maven
- https://github.com/actions/upload-artifact
- https://gist.github.com/bitfexl/51bfedb39a1096ebe40c1fac084bb0b7

and generates notam data json files for the notam map deployment repo.

Overview

- Build app
- Generate json data
- Upload json data to https://github.com/bitfexl/notammapdeployment
