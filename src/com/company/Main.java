package com.company;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private static JFrame frame2 = new JFrame("Zderzenia");;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Kuleczki");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new Panel());
        frame.setPreferredSize(new Dimension(800,600));
        frame.pack();
        frame.setVisible(true);


    }

    static void otowrzOkno() {
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame2.getContentPane().add(new ObslugaPliku());
        frame2.setPreferredSize(new Dimension(800,600));
        frame2.pack();
        frame2.setLocation(810, 0); //pozycja okna na ekranie
        frame2.setVisible(true);
        frame2.setBackground(Color.BLACK);
    }

    static void zamknijOkno() {
        frame2.dispose();
    }

}


