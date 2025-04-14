package GUI;

import BusinessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationSetUpFrame extends JFrame {
    private SimulationManager manager;
    private Image backgroundImage = new ImageIcon(getClass().getResource("/sb.png")).getImage();
    private JPanel contentPane = new BackgroundPanel(backgroundImage);
    //inputs
    private JTextField noClientsField = new JTextField();
    private JTextField noQueuesField = new JTextField();
    private JTextField simulationTimeField = new JTextField();
    private JTextField maxArrivalTimeField = new JTextField();
    private JTextField minArrivalTimeField = new JTextField();
    private JTextField maxServiceTimeField = new JTextField();
    private JTextField minServiceTimeField = new JTextField();
    private JComboBox<String> strategyComboBox = new JComboBox<>();

    private JLabel noClientsLabel = new JLabel("Number of clients:");
    private JLabel noQueuesLabel = new JLabel("Number of queues:");
    private JLabel simulationTimeLabel = new JLabel("Simulation time:");
    private JLabel maxArrivalTimeLabel = new JLabel("Max arrival time:");
    private JLabel minArrivalTimeLabel = new JLabel("Min arrival time:");
    private JLabel maxServiceTimeLabel = new JLabel("Max service time:");
    private JLabel minServiceTimeLabel = new JLabel("Min service time:");
    private JLabel selectionPolicyLabel = new JLabel("Select strategy:");

    private JButton startButton = new JButton("Start Simulation");

    public SimulationSetUpFrame(SimulationManager manager, String name) {
        super(name);
        this.manager = manager;
        this.prepareGUI();
    }

    public void prepareGUI() {
        this.setSize(1200, 700);
        JPanel inputPane = new JPanel();
        inputPane.setLayout(new GridLayout(10, 1));
        this.customizeInput();
        inputPane.setBackground(new Color(167, 207, 255));
        inputPane.add(createRow(noClientsField, noClientsLabel));
        inputPane.add(createRow(noQueuesField, noQueuesLabel));
        inputPane.add(createRow(simulationTimeField, simulationTimeLabel));
        inputPane.add(createRow(maxArrivalTimeField, maxArrivalTimeLabel));
        inputPane.add(createRow(minArrivalTimeField, minArrivalTimeLabel));
        inputPane.add(createRow(maxServiceTimeField, maxServiceTimeLabel));
        inputPane.add(createRow(minServiceTimeField, minServiceTimeLabel));
        inputPane.add(createComboBoxRow(strategyComboBox, selectionPolicyLabel));

        contentPane.setLayout(new BorderLayout());
        contentPane.add(inputPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(167, 207, 255));
        buttonPanel.add(startButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        contentPane.setBackground(new Color(167, 207, 255));
        inputPane.setOpaque(false);
        buttonPanel.setOpaque(false);
        this.setContentPane(contentPane);

    }

    public void customizeInput() {
        customizeInputLabel(noClientsLabel);
        customizeInputLabel(noQueuesLabel);
        customizeInputLabel(simulationTimeLabel);
        customizeInputLabel(maxArrivalTimeLabel);
        customizeInputLabel(minArrivalTimeLabel);
        customizeInputLabel(maxServiceTimeLabel);
        customizeInputLabel(minServiceTimeLabel);
        customizeInputLabel(selectionPolicyLabel);

        customizeInputText(noClientsField);
        customizeInputText(noQueuesField);
        customizeInputText(simulationTimeField);
        customizeInputText(maxArrivalTimeField);
        customizeInputText(minArrivalTimeField);
        customizeInputText(maxServiceTimeField);
        customizeInputText(minServiceTimeField);

        customizeComboBox(strategyComboBox);
        customizeStartButton(startButton);
    }

    public JPanel createRow(JTextField textField, JLabel label){
        JPanel row = new JPanel(new GridLayout(1, 2));
        row.add(label);
        row.add(textField);
        row.setBackground(new Color(167, 207, 255));
        row.setOpaque(false);

        return row;
    }

    public JPanel createComboBoxRow(JComboBox<String> comboBox, JLabel label){
        JPanel row = new JPanel(new GridLayout(1, 2));
        String[] strategies = {"shortest time", "shortest queue"};
        comboBox.setModel(new DefaultComboBoxModel<>(strategies));
        row.setBackground(new Color(167, 207, 255));
        row.setOpaque(false);
        row.add(label);
        row.add(comboBox);
        return row;
    }

    public void customizeInputLabel(JLabel label) {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, 70));
        label.setFont(new Font("Consolas", Font.BOLD, 24));
        label.setForeground(new Color(80, 0, 120));
        label.setBackground(new Color(167, 207, 255));
    }

    public void customizeInputText(JTextField textField){
        textField.setFont(new Font("Consolas", Font.BOLD, 24));
        textField.setForeground(new Color(80, 0, 120));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    public void customizeComboBox(JComboBox<String> comboBox) {
        comboBox.setEditable(true);
        JTextField editor = (JTextField) comboBox.getEditor().getEditorComponent();
        editor.setForeground(new Color(80, 0, 120));
        editor.setHorizontalAlignment(SwingConstants.CENTER);
        editor.setEditable(false);
        comboBox.setFont(new Font("Consolas", Font.BOLD, 22));
        comboBox.setForeground(new Color(80, 0, 120));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Consolas", Font.BOLD, 18));
                label.setForeground(new Color(80, 0, 120));
                return label;
            }
        });
    }

    public void customizeStartButton(JButton startButton) {
        startButton.setFont(new Font("Consolas", Font.BOLD, 24));
        startButton.setPreferredSize(new Dimension(400, 70));
        startButton.setBackground(new Color(0, 200, 0));
        startButton.setForeground(Color.DARK_GRAY);

        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 3, 3, 3, Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.validateInput();
//                if (manager.getStart()) {
//                    Thread simulationThread = new Thread(manager);
//                    simulationThread.start();
//                }
            }
        });
       // startButton.setForeground(Color.WHITE);
    }

    public String getNoClientsFieldText() {
        return noClientsField.getText();
    }

    public String getNoQueuesFieldText() {
        return noQueuesField.getText();
    }

    public String getSimulationTimeFieldText() {
        return simulationTimeField.getText();
    }

    public String getMaxArrivalTimeFieldText() {
        return maxArrivalTimeField.getText();
    }

    public String getMinArrivalTimeFieldText() {
        return minArrivalTimeField.getText();
    }

    public String getMaxServiceTimeFieldText() {
        return maxServiceTimeField.getText();
    }

    public String getMinServiceTimeFieldText() {
        return minServiceTimeField.getText();
    }

    public String getSelectionPolicyItem(){
        return (String) strategyComboBox.getSelectedItem();
    }

}
