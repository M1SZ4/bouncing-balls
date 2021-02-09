package com.company;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private static JFrame frame2 = new JFrame("Zderzenia");

//    configuration of the window in which the balls move, it open automatically when the program starts
    public static void main(String[] args) {
        JFrame frame = new JFrame("Kuleczki");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new Panel());
        frame.setPreferredSize(new Dimension(800,600));
        frame.pack();
        frame.setVisible(true);

    }

//    configuration of the second window which is use to display balls collisions,
//    it opens when when the cursor moves out of the first window,
//    and closes automatically when the cursor returns to the first window
    static void openWindow() {
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.getContentPane().add(new CollisionView());
        frame2.setPreferredSize(new Dimension(800,600));
        frame2.pack();
        frame2.setLocation(810, 0);
        frame2.setVisible(true);
        frame2.setBackground(Color.BLACK);

    }

//  close window with balls collisions
    static void closeWindow() {
        frame2.dispose();
    }

}


