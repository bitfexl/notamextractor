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
    public static void main(String[] args) {
        Map<String, ICAOLocationQuery> locations = ICAOLocationQuery.initialQuery().querySubLocations();
        Map<String, ICAOLocation> austria = locations.get("Germany").queryICAOLocation();

        DINSClient dinsClient = new DINSClient();
        NotamParser parser = new NotamParser();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

        List<String> queryLocations = austria.keySet().stream().toList();
        List<String> notams = dinsClient.byICAOIdentifiersNoLimit(queryLocations);
        List<Notam> parsedNotams = new ArrayList<>();

        for (String rawNotam : notams) {
            parsedNotams.add(parser.parse(rawNotam));
        }

        System.out.println(gson.toJson(parsedNotams));
        System.out.println(parsedNotams.size() + " Notams for Germany");
    }
}