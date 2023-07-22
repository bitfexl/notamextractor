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

**package.yml**:

This action is based on

- https://docs.github.com/en/actions/automating-builds-and-tests/building-and-testing-java-with-maven
- https://github.com/actions/upload-artifact

and exports a jar as an artifact.
