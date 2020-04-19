package com.company;


import java.io.FileWriter;
import java.io.IOException;


public class ObslugaPliku {
    private static int zapisCounter = 0;
    private static String dane = "";

    public static void zapis(int x, int y, int rozmiar, int iloscKul) {
        try{
            if (iloscKul > 10) {
                // jezeli jest wiecej niz 10 kul, zderzenia zapisywane sa po 20
                dane += (x + ", " + y + ", " + rozmiar + "\n");
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
                f.append(x + ", " + y + ", " + rozmiar + "\n");
                f.close();
            }


        }catch(IOException e){
            e.printStackTrace();

        }finally {

        }

    }

}
