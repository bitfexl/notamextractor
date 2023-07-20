package com.github.bitfexl;

import com.github.bitfexl.dins.DINSClient;
import com.github.bitfexl.dins.icao.ICAOLocation;
import com.github.bitfexl.dins.icao.ICAOLocationQuery;
import com.github.bitfexl.notamparser.Notam;
import com.github.bitfexl.notamparser.NotamParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Gson gson = new GsonBuilder()/*.serializeNulls().setPrettyPrinting()*/.create();

    public static void main(String[] args) {
        if (args.length == 0 || "help".equals(args[0])) {
            displayUsage();
            return;
        }

        try {
            switch (args[0]) {
                case "countries": print(listCountries()); break;
                case "icaos": print(listICAOLocations(args[1])); break;
                case "notams": print(listNotams(new ArrayList<>(List.of(args)).subList(1, args.length))); break;
                case "countrynotams": print(listNoatmsByCountry(args[1])); break;
                default: displayUsage(); return;
            }
        } catch (IndexOutOfBoundsException ex) {
            displayUsage();
        }
    }

    private static void print(Object result) {
        System.out.println(gson.toJson(result));
    }

    private static void displayUsage() {
        System.out.println("Usage:");
        System.out.println("List Available Countries: \"./PROGRAM countries\"");
        System.out.println("List ICAO Locations of Country: \"./PROGRAM icaos <Country Name>\"");
        System.out.println("List NOTAMS by ICAOs: \"./PROGRAM notams <ICAO> [<ICAO>] ...\"");
        System.out.println("List NOTAMS by Country: \"./PROGRAM countrynotmas <Country Name>\"");
    }

    private static List<String> listCountries() {
        Map<String, ICAOLocationQuery> locations = ICAOLocationQuery.initialQuery().querySubLocations();

        return locations.keySet().stream().sorted().toList();
    }

    private static List<String> listICAOLocations(String country) {
        // todo: "United States: ..."
        if (country.startsWith("United States")) {
            throw new UnsupportedOperationException("United States are currently not supported.");
        }

        Map<String, ICAOLocationQuery> locations = ICAOLocationQuery.initialQuery().querySubLocations();
        Map<String, ICAOLocation> countryICAOS = locations.get(country).queryICAOLocation();

        return countryICAOS.keySet().stream().sorted().toList();
    }

    private static List<Notam> listNotams(List<String> ICAOs) {
        DINSClient dinsClient = new DINSClient();
        NotamParser parser = new NotamParser();

        List<String> notams = dinsClient.byICAOIdentifiersNoLimit(ICAOs);
        List<Notam> parsedNotams = new ArrayList<>();

        for (String rawNotam : notams) {
            parsedNotams.add(parser.parse(rawNotam));
        }

        return parsedNotams;
    }

    private static List<Notam> listNoatmsByCountry(String country) {
        return listNotams(listICAOLocations(country));
    }
}