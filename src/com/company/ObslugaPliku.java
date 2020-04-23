package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class ObslugaPliku extends JPanel {
    private ArrayList<Kula> listaKul;
    private static int zapisCounter = 0;
    private static String dane = "";


    public ObslugaPliku() {
        listaKul = new ArrayList<>();
        rysujZderzenia();
    }


    public void paint(Graphics g) {
        g.setColor(Color.red);
        for (Kula k : listaKul) {
            g.drawOval(k.x - k.promien, k.y - k.promien, k.promien * 2, k.promien * 2);
        }
    }


    public static void zapis(int x1, int y1, int rozmiar1, int x2, int y2, int rozmiar2, int iloscKul) {
        try{
            String x = (x1 + ", " + y1 + ", " + rozmiar1 + ", " + x2 + ", " + y2 + ", " + rozmiar2 + "\n");

            if (iloscKul > 10) {
                // jezeli jest wiecej niz 10 kul, zderzenia zapisywane sa po 20
                dane += x;
                zapisCounter++;
                if (zapisCounter == 20) {
                    FileWriter f = new FileWriter("test.txt", true);
                    f.append(dane);
                    f.close();
                    zapisCounter = 0;
                    dane = "";
                }
            }

            else {
                FileWriter f = new FileWriter("test.txt", true);
                f.append(x);
                f.close();
            }

        } catch(IOException e) {
            e.printStackTrace();

        } finally {

        }
    }


    public void rysujZderzenia() {
        try{
            int x1, y1, s1, x2, y2, s2;
            File file = new File("test.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String[] liczby = scanner.nextLine().split(", ");
                x1 = Integer.parseInt(liczby[0]);
                y1 = Integer.parseInt(liczby[1]);
                s1 = Integer.parseInt(liczby[2]);

                x2 = Integer.parseInt(liczby[3]);
                y2 = Integer.parseInt(liczby[4]);
                s2 = Integer.parseInt(liczby[5]);

                listaKul.add(new Kula(x1, y1, s1));
                listaKul.add(new Kula(x2, y2, s2));
            }

            scanner.close();

        } catch(IOException e) {
            e.printStackTrace();

        } finally {

        }
        repaint();
    }


    private class Kula {
        public int x, y, promien;

        public Kula(int x, int y, int promien) {
            this.x = x;
            this.y = y;
            this.promien = promien;
        }
    }
}
