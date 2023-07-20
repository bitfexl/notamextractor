package com.github.bitfexl.dins;

import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DINSClient {
    private static final String API_BASE_URL = "https://www.notams.faa.gov/dinsQueryWeb/";

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .callTimeout(Duration.ZERO)
            .readTimeout(Duration.ZERO)
            .build();
    private final DINSExtractor extractor = new DINSExtractor();

    /**
     * Query up to n ICAO identifiers. If the number of identifiers
     * exceeds 50 multiple requests are sent.
     * @param ICAOIdentifiers The identifiers to query.
     * @return A list of found notams (raw text).
     */
    public List<String> byICAOIdentifiersNoLimit(List<String> ICAOIdentifiers) {
        final List<String> notams = new ArrayList<>();

        int i = 0;
        final int step = 50;

        while (i < ICAOIdentifiers.size()) {
            notams.addAll(byICAOIdentifiers(ICAOIdentifiers.subList(i, Math.min(i + step, ICAOIdentifiers.size()))));
            i += step;
        }

        return notams;
    }

    /**
     * Query up to 50 ICAO identifiers.
     * @param ICAOIdentifiers The identifiers to query.
     * @return A list of found notams (raw text).
     */
    public List<String> byICAOIdentifiers(List<String> ICAOIdentifiers) {
        if (ICAOIdentifiers.size() > 50) {
            throw new IllegalArgumentException("Can only query up to 50 ICAO identifiers at once.");
        }

        final RequestBody body = new FormBody.Builder()
                .add("reportType", "Raw")
                .add("actionType", "notamRetrievalByICAOs")
                .add("submit", "View NOTAMs")
                .add("retrieveLocId", ICAOIdentifiers.stream().reduce("", (a, b) -> a + " " + b))
                .build();

        final Request request = new Request.Builder()
                .url(API_BASE_URL + "queryRetrievalMapAction.do")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            final ResponseBody rBody = response.body();

            if (!response.isSuccessful() || rBody == null) {
                throw new RuntimeException("Querying notams failed.");
            }

            return extractor.extractNotams(rBody.string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
