package GUI;

import BusinessLogic.SimulationManager;
import Model.Task;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class LiveSimulationFrame extends JFrame {
    private SimulationManager manager;
    private JPanel contentPane;
    private JPanel tablePanel;
    private List<PacmanQueuePanel> queueRows = new ArrayList<>();
    private JLabel clockLabel;
    private JLabel waitingClientsLabel;
    private JLabel avgWaitingLabel;
    private JLabel avgServiceLabel;
    private JLabel peakHourLabel;
    private JLabel simulationOverLabel;

    public LiveSimulationFrame(SimulationManager sm, String name) {
        super(name);
        this.manager = sm;
        this.prepareWindow();
    }

    private void prepareWindow() {
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout());
        int queueCount = manager.getScheduler().getServers().size();
        tablePanel = new JPanel(new GridLayout(queueCount, 1, 10, 10));
        tablePanel.setOpaque(false);

        for (int i = 0; i < queueCount; i++) {
            PacmanQueuePanel row = new PacmanQueuePanel("Queue " + (i + 1));
            queueRows.add(row);
            tablePanel.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("/hourglass.png"));
        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);

        clockLabel = new JLabel(": 0", scaledIcon, JLabel.LEFT);
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 34));
        clockLabel.setForeground(Color.BLACK);
        clockLabel.setIconTextGap(10);
        clockLabel.setHorizontalAlignment(SwingConstants.LEFT);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        clockLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        waitingClientsLabel = new JLabel("Waiting clients: ");
        waitingClientsLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        waitingClientsLabel.setForeground(Color.DARK_GRAY);
        waitingClientsLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 10));
        waitingClientsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        avgWaitingLabel = new JLabel("Avg waiting time: -");
        avgServiceLabel = new JLabel("Avg service time: -");
        peakHourLabel = new JLabel("Peak hour: -");

        Font statFont = new Font("Consolas", Font.PLAIN, 16);
        avgWaitingLabel.setFont(statFont);
        avgServiceLabel.setFont(statFont);
        peakHourLabel.setFont(statFont);

        avgWaitingLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        avgServiceLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        peakHourLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setOpaque(false);
        statsPanel.add(avgWaitingLabel);
        statsPanel.add(avgServiceLabel);
        statsPanel.add(peakHourLabel);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(clockLabel);
        topPanel.add(waitingClientsLabel);
        topPanel.add(statsPanel);

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        simulationOverLabel = new JLabel("SIMULATION OVER");
        simulationOverLabel.setFont(new Font("Consolas", Font.BOLD, 48));
        simulationOverLabel.setForeground(Color.RED);
        simulationOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        simulationOverLabel.setVerticalAlignment(SwingConstants.CENTER);
        simulationOverLabel.setBounds(0, 0, 1000, 600);
        simulationOverLabel.setVisible(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(1000, 600));
        layeredPane.setLayout(null);

        contentPane.setBounds(0, 0, 1000, 600);
        layeredPane.add(contentPane, JLayeredPane.DEFAULT_LAYER);

        layeredPane.add(simulationOverLabel, JLayeredPane.PALETTE_LAYER);

        this.setContentPane(layeredPane);
        this.pack();
    }

    public void updateQueuesDisplay(List<BlockingQueue<Task>> queues) {
        for (int i = 0; i < queues.size(); i++) {
            List<JLabel> clients = new ArrayList<>();
            for (Task task : queues.get(i)) {
                JLabel client = new JLabel("C" + task.getID() + " arrival time: " + task.getArrivalTime() + " service time: " + task.getServiceTime());
                client.setPreferredSize(new Dimension(200, 40));
                client.setOpaque(true);
                client.setBackground(new Color(135, 206, 250));
                client.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                client.setHorizontalAlignment(SwingConstants.CENTER);
                client.setToolTipText("Arrival: " + task.getArrivalTime() + ", Service: " + task.getServiceTime());
                clients.add(client);
            }
            queueRows.get(i).updateClients(clients);
        }
    }

    public void updateClock(int time) {
        SwingUtilities.invokeLater(() -> clockLabel.setText(""+time));
    }

    public void updateWaitingClients(List<Task> waitingClients) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder("Waiting clients: ");
            for (Task task : waitingClients) {
                sb.append("(C").append(task.getID())
                        .append(", arrival: ").append(task.getArrivalTime())
                        .append(", service: ").append(task.getServiceTime())
                        .append(")  ");
            }
            waitingClientsLabel.setText(sb.toString());
        });
    }

    public void updateStatistics(double avgWaiting, double avgService, int peakHour) {
        SwingUtilities.invokeLater(() -> {
            avgWaitingLabel.setText(String.format("Avg waiting time: %.2f", avgWaiting));
            avgServiceLabel.setText(String.format("Avg service time: %.2f", avgService));
            peakHourLabel.setText("Peak hour: " + peakHour);
        });
    }

    public void showSimulationOverMessage() {
        SwingUtilities.invokeLater(() -> {
            simulationOverLabel.setVisible(true);

            Timer blink = new Timer(500, e -> {
                simulationOverLabel.setVisible(!simulationOverLabel.isVisible());
            });
            blink.start();
        });
    }



}
