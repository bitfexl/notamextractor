package com.github.bitfexl.dins.icao;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A query for ico locations listed on https://www.notams.faa.gov/common/icao/.
 */
public class ICAOLocationQuery {
    private static final String LOCATION_FINDER_URL = "https://www.notams.faa.gov/common/icao/";

    private static final String LOCATION_TABLE_REGEX = "<a href=(.*?)>(.*?)</a>";
    private static final Pattern LOCATION_TABLE_PATTERN = Pattern.compile(LOCATION_TABLE_REGEX, Pattern.MULTILINE);

    private static final String ICAO_LOCATION_REGEX = "<td>(.*?)</td><td>(.*?)</td>";
    private static final Pattern ICAO_LOCATION_PATTERN = Pattern.compile(ICAO_LOCATION_REGEX, Pattern.MULTILINE);

    private static final String US_COUNTRY_NAME = "United States";
    private static final String US_STATE_NAME_PREFIX = US_COUNTRY_NAME + ": ";
    private static final String ICAO_US_LOCATION_REGEX = "<td>.*?</td><td>(.*?)</td><td>(.*?)</td><td>.*?</td>";
    private static final Pattern ICAO_US_LOCATION_PATTERN = Pattern.compile(ICAO_US_LOCATION_REGEX, Pattern.MULTILINE);

    /**
     * Query icao locations from the dins static icao listing.
     * @return The initial query (country name is set to "world").
     */
    public static ICAOLocationQuery initialQuery() {
        return new ICAOLocationQuery("world", LOCATION_FINDER_URL);
    }

    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * The name of the country to query.
     * Is set to "world" in case this is the inital query.
     */
    private String countryName;

    /**
     * The url of the location table (link) for that country.
     * The location table for the United States is different.
     */
    private String locationTableUrl;

    private ICAOLocationQuery(String countryName, String locationTableUrl) {
        this.countryName = countryName;
        this.locationTableUrl = locationTableUrl;
    }

    /**
     * Execute the query and query sub locations.
     * Use this method if countryName is "world" or "United States".
     * @return A map of locations and an ICOLocationQuery with which
     * the actual ico locations can be queried if the country name
     * is "world" or the states with the prefix "United States: "
     * if the location (key) is "United States".
     */
    public Map<String, ICAOLocationQuery> querySubLocations() {
        final Request request = new Request.Builder()
                .url(locationTableUrl)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            final ResponseBody rBody = response.body();

            if (!response.isSuccessful() || rBody == null) {
                throw new RuntimeException("Querying countries for ICAO locations failed.");
            }

            return extractLinks(rBody.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute the query and query icao locations.
     * Use this method if countryName is not "world" or "United States".
     * @return A map of icao location ids and an ICAOLocation with the
     * name of the location and the country.
     */
    public Map<String, ICAOLocation> queryICAOLocation() {
        final Request request = new Request.Builder()
                .url(locationTableUrl)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            final ResponseBody rBody = response.body();

            if (!response.isSuccessful() || rBody == null) {
                throw new RuntimeException("Querying ICAO locations failed.");
            }

            return extractICAOLocations(rBody.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, ICAOLocation> extractICAOLocations(String html) {
        final Map<String, ICAOLocation> locations = new HashMap<>();

        final Matcher matcher = countryName.startsWith(US_STATE_NAME_PREFIX) ? ICAO_US_LOCATION_PATTERN.matcher(html) : ICAO_LOCATION_PATTERN.matcher(html);

        while (matcher.find()) {
            final String name = matcher.group(1);
            final String icao = matcher.group(2);

            locations.put(icao, new ICAOLocation(countryName, name, icao));
        }

        return locations;
    }

    private Map<String, ICAOLocationQuery> extractLinks(String html) {
        final Map<String, ICAOLocationQuery> locations = new HashMap<>();

        final Matcher matcher = LOCATION_TABLE_PATTERN.matcher(html);

        while (matcher.find()) {
            final String url = LOCATION_FINDER_URL + matcher.group(1);
            String name = matcher.group(2);

            if (US_COUNTRY_NAME.equals(countryName)) {
                name = US_STATE_NAME_PREFIX + name;
            }

            locations.put(name, new ICAOLocationQuery(name, url));
        }

        return locations;
    }
}
