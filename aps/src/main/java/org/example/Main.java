package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void createApp(HashMap<Integer, Device> devices, HashMap<Integer, JTextField> deviceFields, RequestController requestController) {
        JFrame frame = new JFrame("Мониторинг устройств");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(devices.size(), 2, 10, 10));
        frame.add(panel);

        devices.forEach((id, device) -> {
            JLabel label = new JLabel("Устройство #" + id);
            JTextField field = new JTextField("Idle");
            field.setEditable(false);
            panel.add(label);
            panel.add(field);
            deviceFields.put(id, field);
        });

        JButton nextRequestButton = new JButton("Следующая заявка");
        nextRequestButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextRequestButton.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(nextRequestButton, BorderLayout.SOUTH);

        nextRequestButton.addActionListener(e -> {
            for (Device device : devices.values()) {
                if (device.isFree()) {
                    Request request = requestController.getNextRequest();
                    if (request != null) {
                        new Thread(() -> {
                            try {
                                device.runDevice(request);
                                JTextField field = deviceFields.get(device.getId());
                                field.setText("Заявка №" + request.getId());
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                                System.err.println("Device#" + device.getId() + " was interrupted during processing.");
                            }
                        }).start();
                        break;
                    } else {
                        System.out.println("No pending requests to process.");
                    }
                    break;
                }
            }
        });


        frame.setVisible(true);
    }

    public static void main(String[] args) {
        HashMap<Integer, Source> sources = Source.generateRandomNumberSources();
        HashMap<Integer, Device> devices = new HashMap<>();
        HashMap<Integer, JTextField> deviceFields = new HashMap<>();

        for (int i = 1; i <= 4; i++) {
            devices.put(i, new Device(i));
        }

        RequestController requestController = new RequestController();
        sources.values().forEach(source -> source.getRequests().values().forEach(requestController::addRequest));

        SwingUtilities.invokeLater(() -> createApp(devices, deviceFields, requestController));

        ExecutorService executorService = Executors.newFixedThreadPool(devices.size());
        devices.forEach((id, device) -> executorService.execute(() -> {
            while (RequestController.hasRequsts()) {
                Request request = requestController.getNextRequest();
                if (request != null) {
                    try {
                        device.runDevice(request);

                        SwingUtilities.invokeLater(() -> {
                            JTextField field = deviceFields.get(id);
                            field.setText("Заявка №" + request.getId());
                        });

                        Thread.sleep(500);
                        SwingUtilities.invokeLater(() -> {
                            JTextField field = deviceFields.get(id);
                            field.setText("Idle");
                        });

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Device#" + id + " was interrupted.");
                    }
                }
            }
        }));

        executorService.shutdown();
    }
}
