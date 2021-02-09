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
//    stores information (x, y, radius) about each ball in the window
    private ArrayList<Ball> ballsList;
//    default ball radius
    private int radius = 20;
//    variable used to stop the balls movement when the second window is displayed
    public boolean loop = true;

    public Panel() {
        ballsList = new ArrayList<>();
        setBackground(Color.BLACK);

        addMouseListener(new Listener());
        addMouseMotionListener(new Listener());
        addMouseWheelListener(new Listener());
    }

    @Override
    public void paintComponent(Graphics g){
//        draw ball on the screen
        super.paintComponent(g);
        for (Ball b : ballsList) {
            b.drawVisibilityPolygon((Graphics2D) g, b.x, b.y, b.ballRadius);
        }
//        show balls counter
        g.setColor(Color.YELLOW);
        g.drawString(Integer.toString(ballsList.size()), 40, 40);
    }

    private class Listener implements MouseListener, MouseMotionListener, MouseWheelListener, ActionListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
//            when the mouse button is pressed, add a new ball
            ballsList.add(new Ball(mouseEvent.getX(), mouseEvent.getY(), radius));
            repaint();

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
//            when the cursor returns to the first window, unpause the balls movement and close the second window
            loop = true;
            Main.closeWindow();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
//            when the cursor moves out of the first window, pause the balls movement and open window with collisions
            loop = false;
            Main.openWindow();
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
//            when the mouse button is pressed, add multiple balls
            ballsList.add(new Ball(mouseEvent.getX(), mouseEvent.getY(), radius));
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
//            moving the scroll upwards increases the radius of the ball and downwards decreases
            if (mouseWheelEvent.getWheelRotation() < 0) {
                radius++;
            }
            else {
                if (radius > 1)
                    radius--;
            }
            repaint();
        }
    }

    public class Ball implements Runnable {
//        single ball configuration
        public int x, y, ballRadius, MAX_SPEED = 5;
        public double xSpeed = 0, ySpeed = 0;

//        sound of collision
        File collisionSound = new File("zderzenie.wav");

        public Ball(int x, int y, int ballRadius) {
//            create new ball, give it random speed
            this.x = x;
            this.y = y;
            this.ballRadius = ballRadius;
            while (xSpeed == 0)
                xSpeed = (int) (Math.random() * MAX_SPEED * 2 - MAX_SPEED);

            while (ySpeed == 0)
                ySpeed = (int) (Math.random() * MAX_SPEED * 2 - MAX_SPEED);

//            create new thread and start it
            Thread t = new Thread(this);
            t.start();
        }

        public void update() {
//            variable "loop" controls the pause of the game
            if (loop) {
//                give the ball a speed
                x += xSpeed;
                y += ySpeed;

//                reflections from walls
                if ((x + ballRadius) >= getWidth()) {
                    x = getWidth() - ballRadius;
                    xSpeed = -xSpeed;
                }
                if ((x - ballRadius / 2) <= 0) {
                    x = ballRadius / 2;
                    xSpeed = -xSpeed;
                }
                if ((y + ballRadius) >= getHeight()) {
                    y = getHeight() - ballRadius;
                    ySpeed = -ySpeed;
                }
                if ((y - ballRadius / 2) <= 0) {
                    y = ballRadius / 2;
                    ySpeed = -ySpeed;
                }
            }
        }

        private void drawVisibilityPolygon(Graphics2D g2d, int x, int y, float radius) {
//            draw ball on the screen with fade effect
            Point center = new Point(x, y);
            float[] dist = {0.0f, 0.9f}; // fade size
            Color[] colors = {
                    new Color(255, 255, 255, 255), // fade color
                    new Color(11, 55, 131, 255) // ball color
            };
            drawGradientCircle(g2d, radius, dist, colors, center);
        }

        private void drawGradientCircle(Graphics2D g2d, float radius, float[] dist, Color[] colors, Point2D center) {
//            position of the gradient on the ball
            RadialGradientPaint rgp = new RadialGradientPaint(center, radius, dist, colors);
            g2d.setPaint(rgp);
            g2d.fill(new Ellipse2D.Double(
                    center.getX() - (radius / 1.5),
                    center.getY() - (radius / 1.5),
                    radius * 2, radius * 2)
            );
        }

        public boolean isCollide(Ball ball) {
//            check if there is a collision between two balls
            double distance = sqrt(pow((ball.x - x), 2) + pow((ball.y - y), 2));
            double radiusSum = ballRadius + ball.ballRadius;
//            if there is a collision
            if (distance <= radiusSum) {
//                save balls size and coordinates to file
                CollisionView.saveToFile(x, y, ballRadius, ball.x, ball.y, ball.ballRadius, ballsList.size());

//                prevents the balls from sticking together
                if (ball.x < x) {
                    ball.x -=  2;
                    x +=  2;
                }
                else {
                    ball.x +=  2;
                    x -=  2;
                }

                if (ball.y < y) {
                    ball.y -=  2;
                    y +=  2;
                }
                else {
                    ball.y +=  2;
                    y -=  2;
                }
            }
            return distance <= radiusSum;
        }

        public void collision(Ball ball) {
//            collision of two balls, changes their directions
            xSpeed = -xSpeed;
            ySpeed = -ySpeed;

            ball.xSpeed = -ball.xSpeed;
            ball.ySpeed = -ball.ySpeed;

        }

        @Override
        public void run() {
//            move each ball and check for collisions
            int ballCount = ballsList.size();
            try {
                while (true) {
                    update();
                    if (ballCount >1) {
                        for (int i = 0; i < ballCount-1; i++)
                            if (isCollide(ballsList.get(i))) {
                                collision(ballsList.get(i));
//                                play sound of collision
                                Clip clip = AudioSystem.getClip();
                                clip.open(AudioSystem.getAudioInputStream(collisionSound));
                                clip.start();
                            }
                    }
                    repaint();
                    Thread.sleep(20);
                }
            } catch (InterruptedException | LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }
}
