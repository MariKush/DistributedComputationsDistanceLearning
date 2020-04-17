package game;

import javax.swing.*;
import java.awt.*;

// Опис вікна програми
public class MainFrame extends JFrame {
    MainFrame(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        this.setSize(new Dimension(1000, 700));

        var dim = getToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - 500, dim.height / 2 - 350);
        var panel = new GamePanel(this, 1000, 700);
        add(panel);
        setVisible(true);
    }
}