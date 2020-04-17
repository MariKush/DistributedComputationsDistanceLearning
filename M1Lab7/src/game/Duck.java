package game;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

// Одна з качок, що літає по ігровому полю
public class Duck extends Thread {
    // Положення качки
    int x;
    int y;

    // Швидкість руху качки в обох напрямках
    private int speedX;
    private int speedY;

    // Розміри території, по якій качка може рухатись
    private int skyWidth;
    private int skyHeight;

    // Розміри качки
    int labelWidth = 128;
    int labelHeight = 109;

    private Random random = new Random();
    private JLabel duck;
    private GamePanel panel;
    private ImageIcon duckRight = new ImageIcon("./resources/duckRL.gif");
    private ImageIcon duckLeft = new ImageIcon("./resources/duckLR.gif");

    Duck(int width, int height, GamePanel panel) {
        super();
        this.skyWidth = width;
        this.skyHeight = height - 250;
        this.panel = panel;

        // Качки випадково генеруються, рухаються по полю та зникають при досягненні певної точки, чи якщо в них влучили
        if (random.nextInt() % 2 == 0) {// Качка летить вліво
            duck = new JLabel(duckLeft);
            duck.setSize(new Dimension(labelWidth, labelHeight));
            int type = random.nextInt(3);
            if (type == 0) {//вилітає справа
                x = -labelWidth;
                y = Math.abs(random.nextInt()) % skyHeight;
            } else if (type == 1) {//вилітає зверху
                y = -labelHeight;
                x = Math.abs(random.nextInt()) % skyWidth;
            } else {//вилітає знизу
                y = skyHeight - labelHeight;
                x = Math.abs(random.nextInt()) % skyWidth;
            }
            speedX = Math.abs(random.nextInt(5)) + 1;
        }

        // Качка летить вправо
        else {
            duck = new JLabel(duckRight);
            duck.setSize(new Dimension(labelWidth, labelHeight));
            int type = random.nextInt(3);
            if (type == 0) {//вилітає зліва
                x = skyWidth + labelWidth;
                y = Math.abs(random.nextInt()) % skyHeight;
            } else if (type == 1) {//вилітає зверху
                y = -labelHeight;
                x = Math.abs(random.nextInt()) % skyWidth;
            } else {//вилітає знизу
                y = skyHeight - labelHeight;
                x = Math.abs(random.nextInt()) % skyWidth;
            }
            speedX = -Math.abs(random.nextInt(5)) - 1;
        }

        // Швидкість качки за випадково заданим напрямком
        if (y > skyHeight / 2)
            speedY = -Math.abs(random.nextInt(4)) - 1;
        else speedY = Math.abs(random.nextInt(4)) + 1;
    }

    // Качка летить, поки не покине дозволені межі або поки в неї не влучать
    @Override
    public void run() {
        panel.add(duck);
        boolean flag = true;
        while (!isInterrupted() && flag) {
            int nextX = x + speedX;
            int nextY = y + speedY;
            if (speedX > 0 && nextX > skyWidth || speedX < 0 && nextX < -labelWidth ||
                    speedY > 0 && nextY > skyHeight || speedY < 0 && nextY < -labelHeight)
                flag = false;
            x = nextX;
            y = nextY;
            duck.setLocation(x, y);
            try {
                sleep(Math.abs(random.nextInt(5)) + 20);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
        panel.remove(duck);
        panel.repaint();
        panel.ducks.remove(this);
    }
}