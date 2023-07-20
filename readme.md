# Notam Extractor

This app extracts notams for a given ICAO location or for a given country from the DINS (https://www.notams.faa.gov/dinsQueryWeb/).

The notams get parsed and converted to java classes and then to json.

## Generate typescript interfaces

Run:

````
mvn process-classes
````

to generate typescript interfaces for the notam data class.