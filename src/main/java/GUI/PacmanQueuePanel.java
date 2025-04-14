package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PacmanQueuePanel extends JPanel {
    private JPanel clientsPanel;
    private JLabel pacmanLabel;
    private Timer animationTimer;
    private ImageIcon[] pacmanFrames;
    private int frameIndex = 0;

    public PacmanQueuePanel(String queueName) {
        setLayout(new BorderLayout(10, 0));
        setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createTitledBorder(queueName));

        ImageIcon open = new ImageIcon(getClass().getResource("/pacman_open.png"));
        ImageIcon closed = new ImageIcon(getClass().getResource("/pacman_closed.png"));

        int pacSize = 40;
        ImageIcon scaledOpen = new ImageIcon(open.getImage().getScaledInstance(pacSize, pacSize, Image.SCALE_SMOOTH));
        ImageIcon scaledClosed = new ImageIcon(closed.getImage().getScaledInstance(pacSize, pacSize, Image.SCALE_SMOOTH));
        pacmanFrames = new ImageIcon[]{ scaledOpen, scaledClosed };

        pacmanLabel = new JLabel(pacmanFrames[0]);
        pacmanLabel.setPreferredSize(new Dimension(pacSize + 10, pacSize + 10));
        pacmanLabel.setAlignmentY(Component.TOP_ALIGNMENT);

        JPanel pacPanel = new JPanel();
        pacPanel.setLayout(new BoxLayout(pacPanel, BoxLayout.Y_AXIS));
        pacPanel.setOpaque(false);
        pacPanel.add(Box.createVerticalStrut(10));
        pacPanel.add(pacmanLabel);

        clientsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientsPanel.setOpaque(false);
        clientsPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        add(pacPanel, BorderLayout.WEST);
        add(clientsPanel, BorderLayout.CENTER);

        animationTimer = new Timer(300, e -> {
            frameIndex = (frameIndex + 1) % pacmanFrames.length;
            pacmanLabel.setIcon(pacmanFrames[frameIndex]);
        });
        animationTimer.start();
    }


    public void updateClients(List<JLabel> clientLabels) {
        clientsPanel.removeAll();
        for (JLabel label : clientLabels) {
            clientsPanel.add(label);
        }
        clientsPanel.revalidate();
        clientsPanel.repaint();
    }
}
