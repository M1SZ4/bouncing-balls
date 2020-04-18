package com.company;

import java.io.FileWriter;
import java.io.IOException;


public class ObslugaPliku {

    public void zapis(int x, int y, int rozmiar) throws IOException {
        FileWriter f = new FileWriter("test.txt");
        f.append(x + ", " + y + ", " + rozmiar + "\n");
        f.close();
    }

}
