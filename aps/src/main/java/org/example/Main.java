package org.example;

// Java program using label (swing)
// to display the message “GFG WEB Site Click”
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import javax.swing.*;

// Main class
class Main {

    public static void createApp() {
        // Create the main frame
        JFrame frame = new JFrame("Dynamic Data Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Create a panel with a grid layout (4 rows, 2 columns)
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 10)); // 4 rows, 2 columns, with spacing

        // Labels and text fields
        JLabel label1 = new JLabel("Источник 1:");
        JTextField field1 = new JTextField();
        field1.setEditable(false); // Make it display-only

        JLabel label2 = new JLabel("Источник 2:");
        JTextField field2 = new JTextField();
        field2.setEditable(false);

        JLabel label3 = new JLabel("Источник 3:");
        JTextField field3 = new JTextField();
        field3.setEditable(false);

        JLabel label4 = new JLabel("Источник 4:");
        JTextField field4 = new JTextField();
        field4.setEditable(false);

        // Add components to the panel
        panel.add(label1);
        panel.add(field1);

        panel.add(label2);
        panel.add(field2);

        panel.add(label3);
        panel.add(field3);

        panel.add(label4);
        panel.add(field4);

        // Add panel to the frame
        frame.add(panel);

        // Set frame visibility
        frame.setVisible(true);

        // Simulate dynamic data update (example)
        new Timer(1000, e -> {
            field1.setText("Data 1: " + Math.random());
            field2.setText("Data 2: " + Math.random());
            field3.setText("Data 3: " + Math.random());
            field4.setText("Data 4: " + Math.random());
        }).start();
    }

    // Main driver method
    public static void main(String[] args)
    {
//        createApp();
        HashMap<Integer, Source> sourcesMap = new HashMap<>();
        sourcesMap = Source.generateRandomNumberSources();
        System.out.println(sourcesMap);
    }
}
