package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.*;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class DeviceChart {
    private final HashMap<Integer, TimeSeries> deviceSeriesMap = new HashMap<>();
    private final JFrame frame;

    public DeviceChart(HashMap<Integer, Device> devices) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        // Создаем временные ряды для каждого устройства
        devices.forEach((id, device) -> {
            TimeSeries series = new TimeSeries("Устройство #" + id);
            dataset.addSeries(series);
            deviceSeriesMap.put(id, series);
        });

        // Создаем график
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Активность устройств",
                "Время",
                "Состояние",
                dataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        frame = new JFrame("График активности");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
    }

    public void showChart() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }

    public void updateDeviceState(int deviceId, long startTime, long duration) {
        TimeSeries series = deviceSeriesMap.get(deviceId);
        if (series != null) {
            Millisecond start = new Millisecond(new java.util.Date(startTime));
            Millisecond end = new Millisecond(new java.util.Date(startTime + duration));

            series.addOrUpdate(start, 1);  // Устройство активно
            series.addOrUpdate(end, 0);   // Устройство становится idle
        }
    }
}
