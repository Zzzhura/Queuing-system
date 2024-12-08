package org.example;

import java.util.HashMap;

public class Device implements Runnable {
    private Integer id;
    private boolean isFree = true;

    public Device(Integer id) {
        this.id = id;
    }

    public boolean isFree() {
        return isFree;
    }

    public Integer getId() {
        return this.id;
    }

    @Override
    public void run() {
        // Поток устройства работает и может обрабатывать запросы, передаваемые извне
    }

    public void runDevice(Request request) throws InterruptedException {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        isFree = false;
        System.out.println("Device#" + this.id + " is working with request#" + request.getId() + " from the source#" + request.getSourceId() + ". \n");

        // Обработка запроса (симуляция работы)
        Thread.sleep(request.getTimeToHandleRequest());

        isFree = true;
        System.out.println("Device#" + this.id + " finished processing request#" + request.getId());
    }

    public void processRequest(Request request) throws InterruptedException {
        isFree = false;
        System.out.println("Device#" + this.id + " is working with request#" + request.getId() + " from the source#" + request.getSourceId() + ".");

        // Симуляция обработки запроса
        Thread.sleep(request.getTimeToHandleRequest());

        isFree = true;
        System.out.println("Device#" + this.id + " has finished processing request#" + request.getId() + ".");
    }

    public static HashMap<Integer, Device> createDevices(int numberOfDevices) {
        HashMap<Integer, Device> devices = new HashMap<>();
        for (int i = 1; i <= numberOfDevices; i++) {
            Device device = new Device(i);
            new Thread(device).start(); // Запуск устройства в отдельном потоке
            devices.put(i, device);
        }
        return devices;
    }
}
