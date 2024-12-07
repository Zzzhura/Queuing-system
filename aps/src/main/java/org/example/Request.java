package org.example;

public class Request {
    private Integer id;
    private Integer sourceId;
    private Integer timeToHandleRequest;

    public Request(Integer requestId, Integer sourceId) {
        this.sourceId = sourceId;
        this.id = requestId;
        this.timeToHandleRequest = (int) (Math.random() * 50);
    }

    public Integer getId() {
        return id;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public Integer getTimeToHandleRequest() {
        return timeToHandleRequest;
    }
}
