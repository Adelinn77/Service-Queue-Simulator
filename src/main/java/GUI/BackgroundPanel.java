package GUI;

import javax.swing.*;
import java.awt.*;

class BackgroundPanel extends JPanel {
    private Image bgImage;

    public BackgroundPanel(Image bgImage) {
        this.bgImage = bgImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}