package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ConcurrentLinkedQueue;

// Ігрове поле
public class GamePanel extends JPanel {
    // Розміри поля
    int width;
    int height;

    private GamePanel panel;
    private ImageIcon background;
    private int maxDucks;
    ConcurrentLinkedQueue<Duck> ducks;
    private Hunter hunter;
    private MainFrame mainFrame;

    GamePanel(MainFrame mainFrame, int width, int height) {
        setBackground(Color.WHITE);
        this.mainFrame = mainFrame;
        panel = this;
        background = new ImageIcon("./resources/background.png");
        maxDucks = 15;
        ducks = new ConcurrentLinkedQueue<>();
        hunter = null;
        this.width = width;
        this.height = height;

        setLayout(null);
        setSize(width, height);
        var mouseAdapter = new GameMouseAdapter();
        addMouseListener(mouseAdapter);
        var game = new Game();
        game.start();
    }

    // Симуляція процесу гри
    class Game extends Thread {
        @Override
        public void run() {
            if (hunter == null) {
                hunter = new Hunter(mainFrame, panel);
                hunter.start();
            }

            // Баланс кількості качок
            while (!isInterrupted()) {
                if (ducks.size() < maxDucks) {
                    var duck = new Duck(width, height, panel);
                    ducks.add(duck);
                    duck.start();
                }
                try {
                    sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    // Обробка натиснення на качку
    class GameMouseAdapter extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            for (var duck : ducks)
                if (e.getX() >= duck.x && e.getX() <= duck.x + duck.labelWidth &&
                        e.getY() >= duck.y && e.getY() <= duck.y + duck.labelHeight)
                    duck.interrupt();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, width, height, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(width, height);
    }
}