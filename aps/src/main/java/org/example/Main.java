package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    static class TimeBar extends JPanel {
        private int length = 0; // Length of the time bar

        public void setLength(int length) {
            this.length = length;
            repaint(); // Repaint the panel to update the length
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            g.fillRect(0, 0, length, getHeight());
        }
    }

    public static void createApp(HashMap<Integer, Device> devices, HashMap<Integer, JTextField> deviceFields, RequestController requestController, HashMap<Integer, TimeBar> timeBars) {
        JFrame frame = new JFrame("Мониторинг устройств");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(devices.size(), 3, 10, 10));
        frame.add(panel);

        // Create and add components for each device
        devices.forEach((id, device) -> {
            JLabel label = new JLabel("Устройство #" + id);
            JTextField field = new JTextField("Idle");
            field.setEditable(false);

            TimeBar timeBar = new TimeBar();
            timeBar.setBackground(Color.LIGHT_GRAY);
            timeBar.setPreferredSize(new Dimension(150, 20));

            panel.add(label);
            panel.add(field);
            panel.add(timeBar);

            deviceFields.put(id, field);
            timeBars.put(id, timeBar);
        });

        // Create a button to simulate the arrival of requests
        JButton nextRequestButton = new JButton("Следующая заявка");
        nextRequestButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextRequestButton.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(nextRequestButton, BorderLayout.SOUTH);

        ExecutorService executorService = Executors.newFixedThreadPool(devices.size());
        LinkedBlockingQueue<Request> requestQueue = new LinkedBlockingQueue<>();

        // Start a thread for each device
        for (Device device : devices.values()) {
            executorService.submit(() -> {
                while (true) {
                    try {
                        Request request = requestQueue.take(); // Wait for a new request
                        if (device.isFree()) {
                            processRequest(device, request, deviceFields, timeBars);
                        } else {
                            requestQueue.put(request); // If device is busy, put the request back
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Device #" + device.getId() + " interrupted.");
                        break;
                    }
                }
            });
        }

        // Button action to add a new request to the queue
        nextRequestButton.addActionListener(e -> {
            System.out.println("Button clicked! New request added to queue.");
            Request request = requestController.getNextRequest();
            if (request != null) {
                try {
                    requestQueue.put(request); // Add the new request to the queue
                    System.out.println("Request " + request.getId() + " added to the queue.");
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    System.err.println("Failed to add request to the queue.");
                }
            } else {
                System.out.println("No request to process.");
            }
        });

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private static void processRequest(Device device, Request request, HashMap<Integer, JTextField> deviceFields, HashMap<Integer, TimeBar> timeBars) {
        System.out.println("Processing request " + request.getId() + " on Device #" + device.getId());

        try {
            long startTime = System.currentTimeMillis();
            device.runDevice(request);
            long endTime = System.currentTimeMillis();
            int processingTime = (int) (endTime - startTime);

            SwingUtilities.invokeLater(() -> {
                TimeBar timeBar = timeBars.get(device.getId());
                timeBar.setLength(processingTime);

                JTextField field = deviceFields.get(device.getId());
                field.setText("Заявка №" + request.getId());
            });

            Thread.sleep(500); // Simulate idle time after processing
            SwingUtilities.invokeLater(() -> {
                JTextField field = deviceFields.get(device.getId());
                field.setText("Idle");
                TimeBar timeBar = timeBars.get(device.getId());
                timeBar.setLength(0);
            });
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("Device #" + device.getId() + " was interrupted during processing.");
        }
    }

    public static void main(String[] args) {
        // Generate sources and requests
        HashMap<Integer, Source> sources = Source.generateRandomNumberSources();
        HashMap<Integer, Device> devices = new HashMap<>();
        HashMap<Integer, JTextField> deviceFields = new HashMap<>();
        HashMap<Integer, TimeBar> timeBars = new HashMap<>();

        for (int i = 1; i <= 4; i++) {
            devices.put(i, new Device(i));
        }

        RequestController requestController = new RequestController();
        sources.values().forEach(source -> source.getRequests().values().forEach(requestController::addRequest));

        SwingUtilities.invokeLater(() -> createApp(devices, deviceFields, requestController, timeBars));
    }
}
