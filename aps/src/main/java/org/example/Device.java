package org.example;

import java.util.HashMap;

public class Device {
    private boolean isWorking = false;
    private boolean isFree = true;
    private Request request;
    private Integer id;

    public HashMap< Integer, Device > createDevices(Request request){
        HashMap< Integer, Device > resultMap = new HashMap<>();
        int numberOfDevices = (int) (Math.random() * 10);
        for (int i = 0; i < numberOfDevices; i++){
            resultMap.put(i, new Device())
        }
    }
}
