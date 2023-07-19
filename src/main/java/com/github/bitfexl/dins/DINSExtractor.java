package com.github.bitfexl.dins;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DINSExtractor {
    private final String REGEX = "<PRE>(.*?)<\\/PRE>";
    private final Pattern PATTERN = Pattern.compile(REGEX, Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * Extract notams from a response form https://www.notams.faa.gov/dinsQueryWeb/.
     * @param html The html returned by the dins query.
     * @return A list of raw notam texts.
     */
    public List<String> extractNotams(String html) {
        final Matcher matcher = PATTERN.matcher(html);

        final List<String> notams = new ArrayList<>();

        while (matcher.find()) {
            notams.add(matcher.group(1));
        }

        return notams;
    }
}
