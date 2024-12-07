package org.example;

import java.util.HashMap;

public class Source {
    private Integer source_id;
    private HashMap<Integer, Request> requests;

    public static HashMap<Integer, Source> generateRandomNumberSources() {
        HashMap<Integer, Source> resultMap = new HashMap<>();
        Integer numberOfSources = (int) (Math.random() * 10);
        for (int i = 0; i < numberOfSources; i++) {
            resultMap.put(i, new Source(i));
        }
        return resultMap;
    }

    public Source(Integer source_id) {
        this.source_id = source_id;
        Integer numberOfRequests = (int) (Math.random() * 100);
        HashMap<Integer, Request> sourceRequests = new HashMap<>();
        for (int i = 0; i < numberOfRequests; i++) {
            sourceRequests.put(i, new Request(i, source_id));
        }
        this.requests = sourceRequests;
    }

    public HashMap<Integer, Request> getRequests() {
        return requests;
    }
}
