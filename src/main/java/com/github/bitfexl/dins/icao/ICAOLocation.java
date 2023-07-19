package com.github.bitfexl.dins.icao;

import lombok.Value;

@Value
public class ICAOLocation {
    /**
     * The country or state this location is located in.
     */
    String country;

    /**
     * The readable name of the location.
     */
    String name;

    /**
     * The icao id of the location.
     */
    String icao;
}
