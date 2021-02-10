package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class CollisionView extends JPanel implements Runnable {
//    stores information (x, y, radius) about every collision of the ball
    private ArrayList<Ball> ballsList;
//    variable is used to buffer writing to a file
    private static int saveCounter = 0;
    private static String dataToSave = "";

    public CollisionView() {
        ballsList = new ArrayList<>();
        Thread t = new Thread(this);
        t.start();
    }


    @Override
    public void run() {
        try {
            readCollisionsCoords();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void paint(Graphics g) {
//        draw each collision on screen
        g.setColor(Color.red);
        for (Ball k : ballsList) {
            g.drawOval(k.x - k.radius, k.y - k.radius, k.radius * 2, k.radius * 2);
        }
    }


    public static void saveToFile(int x1, int y1, int size1, int x2, int y2, int size2, int ballsCount) {
//        save information about each collision in a file 'collisions.txt' (ball1 and ball2 coordinates and size)
        try{
            String x = (x1 + ", " + y1 + ", " + size1 + ", " + x2 + ", " + y2 + ", " + size2 + "\n");

            if (ballsCount > 10) {
//                if there are more than 10 balls, the collisions are recorded with 20 in one save
                dataToSave += x;
                saveCounter++;
                if (saveCounter == 20) {
                    FileWriter f = new FileWriter("collisions.txt", true);
                    f.append(dataToSave);
                    f.close();
                    saveCounter = 0;
                    dataToSave = "";
                }
            }

            else {
                FileWriter f = new FileWriter("collisions.txt", true);
                f.append(x);
                f.close();
            }

        } catch(IOException e) {
            e.printStackTrace();

        }
    }


    public void readCollisionsCoords() {
//        read information about each ball from file and save it in 'ballsList' array
        File file = new File("collisions.txt");
        if (file.exists() && !file.isDirectory()) {
            try {
                int x1, y1, s1, x2, y2, s2;

                Scanner scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    String[] coordinates = scanner.nextLine().split(", ");
                    x1 = Integer.parseInt(coordinates[0]);
                    y1 = Integer.parseInt(coordinates[1]);
                    s1 = Integer.parseInt(coordinates[2]);

                    x2 = Integer.parseInt(coordinates[3]);
                    y2 = Integer.parseInt(coordinates[4]);
                    s2 = Integer.parseInt(coordinates[5]);

                    ballsList.add(new Ball(x1, y1, s1));
                    ballsList.add(new Ball(x2, y2, s2));
                }

                scanner.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }

    private class Ball {
        public int x, y, radius;

        public Ball(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }
}
