package com.company;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class Panel extends JPanel {
    private ArrayList<Kula> listaKul;
    private int promien = 20;
    public boolean petla = true;

    public Panel() {
        listaKul = new ArrayList<>();
        setBackground(Color.BLACK); // kolor tla

        // sledzenie myszki
        addMouseListener(new Listener());
        addMouseMotionListener(new Listener());
        addMouseWheelListener(new Listener());
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        for (Kula k : listaKul) {
            k.drawVisibilityPolygon((Graphics2D) g, k.x, k.y, k.promien);
        }
        //licznik kul
        g.setColor(Color.YELLOW);
        g.drawString(Integer.toString(listaKul.size()), 40, 40);
    }

    private class Listener implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            listaKul.add(new Kula(mouseEvent.getX(), mouseEvent.getY(), promien));
            repaint();

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            petla = true;
            // zamkniecie drugiego okna
            Main.zamknijOkno();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            petla = false;
            // otwarcie drugiego okna
            Main.otowrzOkno();
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
            listaKul.add(new Kula(mouseEvent.getX(), mouseEvent.getY(), promien));
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            if (mouseWheelEvent.getWheelRotation() < 0) {
                promien++;
            }
            else {
                if (promien > 1)
                    promien--;
            }
            repaint();
        }
    }

    public class Kula implements Runnable{
        public int x, y, promien;
        public double xspeed = 0, yspeed = 0;
        private final int MAX_SPEED = 5;
        File zderzenie = new File("zderzenie.wav");

        public Kula(int x, int y, int promien) {
            this.x = x;
            this.y = y;
            this.promien = promien;
            while (xspeed == 0)
                xspeed = (int) (Math.random() * MAX_SPEED * 2 - MAX_SPEED);

            while (yspeed == 0)
                yspeed = (int) (Math.random() * MAX_SPEED * 2 - MAX_SPEED);

            Thread t = new Thread(this);
            t.start();
        }

        public void update() {
            // if jest po to aby zatrzymywac i wznawiac klulki po wyjechaniu myszka za okno
            if (petla) {
                x += xspeed;
                y += yspeed;

                // dobicia od scian
//            if (x - (size / 2) <= 0 || x + size  >= getWidth())
//                xspeed = -xspeed;
//
//            if (y - (size / 2) <= 0 || y + size  >= getHeight())
//                yspeed = -yspeed;

//                "ulepszone" odbicia od scian
                if ((x + promien) >= getWidth()) {
                    x = getWidth() - promien;
                    xspeed = -xspeed;
                }
                if ((x - promien / 2) <= 0) {
                    x = promien / 2; // kulki nie blokuja sie w scianach
                    xspeed = -xspeed;
                }
                if ((y + promien) >= getHeight()) {
                    y = getHeight() - promien;
                    yspeed = -yspeed;
                }
                if ((y - promien / 2) <= 0) {
                    y = promien / 2;
                    yspeed = -yspeed;
                }
            }
        }

        private void drawVisibilityPolygon(Graphics2D g2d, int x, int y, float radius) {
            Point center = new Point(x, y); // srodek kuli
            float[] dist = {0.0f, 0.9f}; // rozmiar gradientu
            Color[] colors = {
                    new Color(255, 255, 255, 255), // kolor gradientu
                    new Color(11, 55, 131, 255) // kolor kuli
            };
            drawGradientCircle(g2d, radius, dist, colors, center);
        }

        private void drawGradientCircle(Graphics2D g2d, float radius, float[] dist, Color[] colors, Point2D center) {
            RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(rgp);
            // umieszczenie gradientu na kuli
            g2d.fill(new Ellipse2D.Double(
                    center.getX() - (radius / 1.5),
                    center.getY() - (radius / 1.5),
                    radius * 2, radius * 2)
            );
        }

        public boolean czyKoliduje(Kula kula) {
            double odleglosc = sqrt(pow((kula.x - x), 2) + pow((kula.y - y), 2));
            double sumaPromieni = promien + kula.promien;
            // zapobiega "sklejaniu" sie kulek
            if (odleglosc <= sumaPromieni) {
                //zapis do pliku
                ObslugaPliku.zapis(x, y, promien, kula.x, kula.y, kula.promien, listaKul.size());
                if (kula.x < x) {
                    kula.x -=  2;
                    x +=  2;
                }
                else {
                    kula.x +=  2;
                    x -=  2;
                }

                if (kula.y < y) {
                    kula.y -=  2;
                    y +=  2;
                }
                else {
                    kula.y +=  2;
                    y -=  2;
                }
            }
            return odleglosc <= sumaPromieni;
        }

        public void kolizja(Kula kula) {
//            "Standardowe" zderzenia
//            xspeed = -xspeed;
//            yspeed = -yspeed;
//
//            kula.xspeed = -kula.xspeed;
//            kula.yspeed = -kula.yspeed;

//            Bardziej realistyczne zderzenia, korzystalem z tej strony:
//            https://code.tutsplus.com/tutorials/playing-around-with-elastic-collisions--active-7472?fbclid=IwAR3v_1DS286xowVLw67t-qALgNvBAYwy5p-0Nvo8Iqn_oJ4htLkAfOyh_xo
            int xDist = kula.x - x;
            int yDist = kula.y - y;
            double collisionAngle = Math.atan2(yDist, xDist);

            double magBall1 = Math.sqrt(kula.xspeed * kula.xspeed + kula.yspeed * kula.yspeed);
            double magBall2 = Math.sqrt(xspeed * xspeed + yspeed * yspeed);

            double angleBall1 = Math.atan2(kula.yspeed, kula.xspeed);
            double angleBall2 = Math.atan2(yspeed, xspeed);

            double xSpeedBall1 = magBall1 * Math.cos(angleBall1-collisionAngle);
            double ySpeedBall1 = magBall1 * Math.sin(angleBall1-collisionAngle);
            double xSpeedBall2 = magBall2 * Math.cos(angleBall2-collisionAngle);
            double ySpeedBall2 = magBall2 * Math.sin(angleBall2-collisionAngle);

            double finalxSpeedBall1 = ((kula.promien - promien) * xSpeedBall1 + (promien + promien) * xSpeedBall2) / (kula.promien + promien);
            double finalxSpeedBall2 = ((kula.promien + kula.promien) * xSpeedBall1 + (promien - kula.promien)* xSpeedBall2) / (kula.promien + promien);
            double finalySpeedBall1 = ySpeedBall1;
            double finalySpeedBall2 = ySpeedBall2;

            kula.xspeed = Math.cos(collisionAngle)*finalxSpeedBall1+Math.cos(collisionAngle+Math.PI/2)*finalySpeedBall1;
            kula.yspeed = Math.sin(collisionAngle)*finalxSpeedBall1+Math.sin(collisionAngle+Math.PI/2)*finalySpeedBall1;
            xspeed = Math.cos(collisionAngle)*finalxSpeedBall2+Math.cos(collisionAngle+Math.PI/2)*finalySpeedBall2;
            yspeed = Math.sin(collisionAngle)*finalxSpeedBall2+Math.sin(collisionAngle+Math.PI/2)*finalySpeedBall2;

        }

        @Override
        public void run() {
            int liczbaKulek = listaKul.size();
            try {
                while (true) {
                    update();
                    if (liczbaKulek >1) {
                        for (int i = 0; i < liczbaKulek-1; i++)
                            if (czyKoliduje(listaKul.get(i))) {
                                kolizja(listaKul.get(i));
                                Clip clip = AudioSystem.getClip();
                                clip.open(AudioSystem.getAudioInputStream(zderzenie));
                                clip.start();
                            }
                    }
                    repaint();
                    Thread.sleep(20);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }
}
