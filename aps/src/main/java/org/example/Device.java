package org.example;

import java.util.HashMap;

public class Device implements Runnable{
    private Integer id;
    private boolean isWorking = false;
    private boolean isFree = true;

    public Device(Integer id){
        this.id = id;
    }

    public boolean isFree(){
        return isFree;
    }

    public Integer getId(){
        return this.id;
    }

    @Override
    public void run(){
        while (RequestController.hasRequsts()){
            Request request = RequestController.getNextRequest();
            if (request != null){
                try{
                    runDevice(request);
                } catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    System.err.println("Device#" + id + " was interrupted.");
                }
            }
        }
    }

    public void runDevice(Request request) throws InterruptedException {
        isFree = false;
        isWorking = true;
        System.out.println("Device#" + this.id + " is working with request#" + request.getId() + " from the source#" + request.getSourceId() + ". \n");
        Thread.sleep(request.getTimeToHandleRequest());
        isFree = true;
        isWorking = false;
    }


    public static HashMap< Integer, Device > createDevices(int numberOfDevices){
        HashMap< Integer, Device > devices = new HashMap<>();
        for (int i = 1; i <= numberOfDevices; i++){
            devices.put(i, new Device(i));
        }
        return devices;
    }
}
