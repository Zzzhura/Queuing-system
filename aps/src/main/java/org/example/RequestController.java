package org.example;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestController {
    private static final ConcurrentLinkedQueue< Request > requestQueue =  new ConcurrentLinkedQueue<>();

    public void addRequest(Request request){
        requestQueue.add(request);
    }

    public static Request getNextRequest(){
        return requestQueue.poll();
    }

    public static boolean hasRequsts(){
        return !requestQueue.isEmpty();
    }
}