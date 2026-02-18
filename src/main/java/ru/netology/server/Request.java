package ru.netology.server;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String pathWithQuery;

    public Request(String method, String pathWithQuery) {
        this.method = method;
        this.pathWithQuery = pathWithQuery;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        int idx = pathWithQuery.indexOf('?');
        return idx == -1 ? pathWithQuery : pathWithQuery.substring(0, idx);
    }

    public Map<String, String> getQueryParams() {
        int idx = pathWithQuery.indexOf('?');
        if (idx == -1) return Map.of();

        String query = pathWithQuery.substring(idx + 1);
        List<NameValuePair> params = URLEncodedUtils.parse(query, StandardCharsets.UTF_8);

        return params.stream()
                .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
    }

    public String getQueryParam(String name) {
        return getQueryParams().get(name);
    }
}
