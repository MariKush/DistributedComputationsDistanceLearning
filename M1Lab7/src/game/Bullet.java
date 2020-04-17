package game;

import javax.swing.*;
import java.awt.*;

// Куля, яку випускає мисливець
public class Bullet extends Thread {
    // Положення кулі на полі
    private int x;
    private int y;

    // Відхилення кулі при її русі вгору
    private int dy = 10;

    // Розміри кулі
    private int labelWidth = 14;
    private int labelHeight = 37;

    private GamePanel panel;
    private JLabel bulletLabel;
    private Hunter hunter;

    Bullet(GamePanel panel, Hunter hunter, int x, int y) {
        this.panel = panel;
        this.hunter = hunter;
        this.x = x;
        this.y = y;
        this.bulletLabel = new JLabel(new ImageIcon("./resources/shot.png"));
        bulletLabel.setSize(new Dimension(labelWidth, labelHeight));
        bulletLabel.setLocation(x - labelWidth / 2, y - labelHeight / 2);
    }

    // Куля вилітає із рушниці
    @Override
    public void run() {
        hunter.addBullet(1);
        panel.add(bulletLabel);
        while (!isInterrupted()) {
            if (y < 0)
                break;
            y -= dy;
            bulletLabel.setLocation(x - labelWidth / 2, y - labelHeight / 2);
            // Куля влучила
            for (Duck duck : panel.ducks)
                if (x > duck.x && x < duck.x + duck.labelWidth && y > duck.y && y < duck.y + duck.labelHeight) {
                    duck.interrupt();
                    this.interrupt();
                    break;
                }
            try {
                sleep(10);
            } catch (InterruptedException e) {
                break;
            }
        }
        panel.remove(bulletLabel);
        panel.repaint();
        hunter.addBullet(-1);
    }
}