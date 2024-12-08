package org.example;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.HashMap;

// RequestBuffer класс для управления очередью и обработкой запросов
public class RequestBuffer {
    private final LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();
    private final HashMap<Integer, Device> devices;

    public RequestBuffer(HashMap<Integer, Device> devices) {
        this.devices = devices;
    }

    public void onDeviceFree(Device device) {
        synchronized (devices) {
            Request nextRequest = requestQueue.poll();
            if (nextRequest != null) {
                assignRequestToDevice(device, nextRequest);
            } else {
                System.out.println("Device #" + device.getId() + " is now free.");
            }
        }
    }

    private void assignRequestToDevice(Device device, Request request) {
        new Thread(() -> {
            try {
                System.out.println("Processing request " + request.getId() + " on Device #" + device.getId());
                device.processRequest(request);
                onDeviceFree(device); // Уведомить после завершения обработки
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("Device #" + device.getId() + " interrupted.");
            }
        }).start();
    }
}
