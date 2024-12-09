package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Main {

    static class TimeBar extends JPanel {
        private int length = 0;

        public void setLength(int length) {
            this.length = length;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, length, getHeight());
        }
    }

    public static void createApp(HashMap<Integer, Device> devices, HashMap<Integer, JTextField> deviceFields, RequestController requestController, HashMap<Integer, TimeBar> timeBars, DeviceChart deviceChart) {
        JFrame frame = new JFrame("Мониторинг устройств");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(devices.size(), 3, 10, 10));
        frame.add(panel);

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

        JButton nextRequestButton = new JButton("Следующая заявка");
        nextRequestButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextRequestButton.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(nextRequestButton, BorderLayout.SOUTH);

        // Создаем RequestBuffer с передачей устройств
        RequestBuffer requestBuffer = new RequestBuffer(devices);

        // Назначаем обработчик для кнопки
        nextRequestButton.addActionListener(e -> {
            System.out.println("Button clicked! New request added to queue.");
            Request request = requestController.getNextRequest();
            if (request != null) {
                // Найти свободное устройство и передать запрос
                for (Device device : devices.values()) {
                    if (device.isFree()) {
                        new Thread(() -> {
                            try {
                                processRequest(device, request, deviceFields, timeBars, deviceChart);
                            } catch (InterruptedException ex) {
                                System.err.println("Error processing request: " + ex.getMessage());
                            }
                        }).start();
                        System.out.println("Request " + request.getId() + " sent to Device #" + device.getId());
                        return;
                    }
                }
                System.out.println("No free devices available to handle request.");
            } else {
                System.out.println("No request to process.");
            }
        });

        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    private static void processRequest(Device device, Request request, HashMap<Integer, JTextField> deviceFields, HashMap<Integer, TimeBar> timeBars, DeviceChart deviceChart) throws InterruptedException {
        System.out.println("Processing request " + request.getId() + " on Device #" + device.getId());

        try {
            long startTime = System.currentTimeMillis();
            device.runDevice(request); // Выполнение запроса (симуляция работы)
            deviceChart.updateDeviceState(device.getId(), startTime, request.getTimeToHandleRequest()); // Обновляем график
            long endTime = System.currentTimeMillis();
            int processingTime = (int) (endTime - startTime);

            SwingUtilities.invokeLater(() -> {
                // Обновление TimeBar и JTextField
                TimeBar timeBar = timeBars.get(device.getId());
                if (timeBar != null) {
                    timeBar.setLength(processingTime);
                }

                JTextField field = deviceFields.get(device.getId());
                if (field != null) {
                    field.setText("Заявка №" + request.getId());
                }
            });

            Thread.sleep(2000); // Пауза для демонстрации обработки

            SwingUtilities.invokeLater(() -> {
                // Сброс состояния TimeBar и JTextField после обработки
                JTextField field = deviceFields.get(device.getId());
                if (field != null) {
                    field.setText("Idle");
                }

                TimeBar timeBar = timeBars.get(device.getId());
                if (timeBar != null) {
                    timeBar.setLength(0);
                }
            });
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            System.err.println("Device #" + device.getId() + " was interrupted during processing.");
        }
    }

    public static void main(String[] args) {
        HashMap<Integer, Source> sources = Source.generateRandomNumberSources();
        HashMap<Integer, Device> devices = Device.createDevices(4); // Создание и запуск устройств
        HashMap<Integer, JTextField> deviceFields = new HashMap<>();
        HashMap<Integer, TimeBar> timeBars = new HashMap<>();

        DeviceChart deviceChart = new DeviceChart(devices);
        deviceChart.showChart(); // Показываем график

        RequestController requestController = new RequestController();
        sources.values().forEach(source -> source.getRequests().values().forEach(requestController::addRequest));

        SwingUtilities.invokeLater(() -> createApp(devices, deviceFields, requestController, timeBars, deviceChart));
    }
}
