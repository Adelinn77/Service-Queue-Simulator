package BusinessLogic;

import GUI.LiveSimulationFrame;
import GUI.SimulationSetUpFrame;
import Model.Server;
import Model.Task;

import javax.swing.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SimulationManager implements Runnable {
    private int timeLimit = -1;
    private int maxServiceTime = -1;
    private int minServiceTime = -1;
    private int maxArrivalTime = -1;
    private int minArrivalTime = -1;
    private int numberOfServers = -1;
    private int numberOfClients = -1;

    private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
    private Scheduler scheduler;
    private SimulationSetUpFrame frame;
    private LiveSimulationFrame liveSimulationFrame;
    private List<Task> generatedTasks;
    private BufferedWriter logWriter;
    private boolean start = false;

    private float averageServiceTime = 0;
    private int peakHour = -1;
    private int totalServersSize = 0;

    public SimulationManager() {
        //scheduler = new Scheduler(numberOfServers, numberOfClients);
        //scheduler.changeStrategy(selectionPolicy);
        frame = new SimulationSetUpFrame(this,"Simulation Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //generatedTasks = generateNRandomTasks();
        setStart(false);
        //generatedTasks = generateExampleTasks();
    }

    private List<Task> generateNRandomTasks() {
        List<Task> tasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            averageServiceTime += serviceTime;
            tasks.add(new Task(i + 1, arrivalTime, serviceTime));
        }
        averageServiceTime /= numberOfClients;
        //System.out.println(tasks);
        tasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        //System.out.println(tasks);
        return tasks;
    }

    private List<Task> generateExampleTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(1, 2, 2));
        tasks.add(new Task(2, 3, 3));
        tasks.add(new Task(3, 4, 3));
        tasks.add(new Task(4, 10, 2));
        return tasks;
    }


    @Override
    public void run() {
        if(getStart()) {
            try {
                logWriter = new BufferedWriter(new FileWriter("log.txt"));
            } catch (IOException e) {
                System.out.println("Error writing log file");
                e.printStackTrace();
            }
            //AtomicInteger currentTime = new AtomicInteger(0);
            int currentTime = 0;
            int maxSize = 0;
            while (currentTime <= timeLimit) {
                Iterator<Task> iterator = generatedTasks.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task.getArrivalTime() == currentTime) {
                        scheduler.dispatchTask(task);
                        iterator.remove();
                    }
                }

                maxSize = 0;
                for (Server s : scheduler.getServers()) {
                    maxSize += s.getTasks().size();
                }
                if (maxSize > totalServersSize) {
                    totalServersSize = maxSize;
                    peakHour = currentTime;
                }

                log("Time " + currentTime);
                log("Waiting clients: " + formatWaitingClients(generatedTasks));

                int index = 1;
                //System.out.println(numberOfClients + " " + scheduler.getServers().size());
                for (Server server : scheduler.getServers()) {
                    BlockingQueue<Task> queue = server.getTasks();
                    if (queue.isEmpty()) {
                        log("Queue " + index + ": closed");
                    } else {
                        log("Queue " + index + ": " + formatQueue(queue));
                    }
                    index++;
                }
                log("");
                List<BlockingQueue<Task>> liveQueues = new ArrayList<>();
                for (Server s : scheduler.getServers()) {
                    liveQueues.add(s.getTasks());
                }
                liveSimulationFrame.updateClock(currentTime);
                liveSimulationFrame.updateQueuesDisplay(liveQueues);
                liveSimulationFrame.updateWaitingClients(generatedTasks);
                //frame.update(currentTime, scheduler.getServers(), generatedTasks);

                currentTime++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

            }
            scheduler.stopAllServers();
            scheduler.setAverageWaitingTime(scheduler.getAverageWaitingTime()/numberOfClients);
            liveSimulationFrame.updateStatistics(scheduler.getAverageWaitingTime(), this.getAverageServiceTime(), this.getPeakHour());
            liveSimulationFrame.showSimulationOverMessage();
            log("Average service time: " + this.getAverageServiceTime());
            log("Peak hour: " + this.getPeakHour());
            log("Average waiting time: " + scheduler.getAverageWaitingTime());
            log("The simulation is finished when the simulation time is over.");
            try {
                logWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Simulation finished");
            System.out.println("average service time: " + this.getAverageServiceTime() + " peak hour: " + this.getPeakHour() + " average waiting time: " + scheduler.getAverageWaitingTime());
        }
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public void setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    public int getNumberOfServers() {
        return numberOfServers;
    }

    public void setNumberOfServers(int numberOfServers) {
        this.numberOfServers = numberOfServers;
    }

    public int getMaxArrivalTime() {
        return maxArrivalTime;
    }

    public void setMaxArrivalTime(int maxArrivalTime) {
        this.maxArrivalTime = maxArrivalTime;
    }

    public int getMinArrivalTime() {
        return minArrivalTime;
    }

    public void setMinArrivalTime(int minArrivalTime) {
        this.minArrivalTime = minArrivalTime;
    }


    public int getMaxServiceTime() {
        return maxServiceTime;
    }

    public void setMaxServiceTime(int maxServiceTime) {
        this.maxServiceTime = maxServiceTime;
    }

    public int getMinServiceTime() {
        return minServiceTime;
    }

    public void setMinServiceTime(int minServiceTime) {
        this.minServiceTime = minServiceTime;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean getStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public List<Task> getGeneratedTasks() {
        return generatedTasks;
    }

    public void setGeneratedTasks(List<Task> generatedTasks) {
        this.generatedTasks = generatedTasks;
    }

    public float getAverageServiceTime() {
        return averageServiceTime;
    }

    public void setAverageServiceTime(float averageServiceTime) {
        this.averageServiceTime = averageServiceTime;
    }

    public void setupScheduler() {
        this.scheduler = new Scheduler(numberOfServers, numberOfClients);
        this.scheduler.changeStrategy(selectionPolicy);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public String toString() {
        return "SimulationManager{" +
                "timeLimit=" + timeLimit +
                ", maxServiceTime=" + maxServiceTime +
                ", minServiceTime=" + minServiceTime +
                ", maxArrivalTime=" + maxArrivalTime +
                ", minArrivalTime=" + minArrivalTime +
                ", numberOfServers=" + numberOfServers +
                ", numberOfClients=" + numberOfClients +
                '}';
    }

    private void log(String message) {
        try {
            logWriter.write(message);
            logWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatWaitingClients(List<Task> tasks) {
        return tasks.stream()
                .map(t -> "(" + t.getID() + "," + t.getArrivalTime() + "," + t.getServiceTime() + ")")
                .collect(Collectors.joining("; "));
    }

    private String formatQueue(BlockingQueue<Task> queue) {
        return queue.stream()
                .map(Task::toString)
                .collect(Collectors.joining("; "));
    }

    public void validateInput(){
        int noClients = -1, noQueues = -1, simTime = -1, maxATime = -1, minATime = -1, maxSTime = -1, minSTime = -1;
        String selectionP = "";
        try {
            noClients = Integer.parseInt(frame.getNoClientsFieldText());
            noQueues = Integer.parseInt(frame.getNoQueuesFieldText());
            //System.out.println("hereeee" + noQueues);
            simTime = Integer.parseInt(frame.getSimulationTimeFieldText());
            maxATime = Integer.parseInt(frame.getMaxArrivalTimeFieldText());
            minATime = Integer.parseInt(frame.getMinArrivalTimeFieldText());
            maxSTime = Integer.parseInt(frame.getMaxServiceTimeFieldText());
            minSTime = Integer.parseInt(frame.getMinServiceTimeFieldText());
            selectionP = frame.getSelectionPolicyItem();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter valid numbers!");
            return;
        }
        boolean validIntervals = validateIntervals(noClients, noQueues, simTime, maxATime, minATime, maxSTime, minSTime);
        if(validIntervals){
            this.setNumberOfClients(noClients);
            this.setNumberOfServers(noQueues);
            //System.out.println("hereeeeeeee" + this.getNumberOfServers());
            this.setTimeLimit(simTime);
            this.setMaxServiceTime(maxSTime);
            this.setMinServiceTime(minSTime);
            this.setMaxArrivalTime(maxATime);
            this.setMinArrivalTime(minATime);
            System.out.println(this);
            this.setupScheduler();
            this.generatedTasks = generateNRandomTasks();
            if(selectionP.equals("SHORTEST TIME")){
                selectionPolicy = SelectionPolicy.SHORTEST_TIME;
                this.scheduler.changeStrategy(selectionPolicy);
            } else if (selectionP.equals("SHORTEST QUEUE")) {
                selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;
                this.scheduler.changeStrategy(selectionPolicy);
            }
            liveSimulationFrame = new LiveSimulationFrame(this,"Real-time simulation");
            //liveSimulationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            liveSimulationFrame.setVisible(true);
            this.setStart(true);
            Thread t = new Thread(this);
            t.start();
        }
    }

    public boolean validateIntervals(int noClients, int noQueues, int simTime, int maxATime, int minATime, int maxSTime, int minSTime) {
        if(noClients < 0 || noQueues < 0 || simTime < 0 || maxATime < 0 || minATime < 0 || maxSTime < 0 || minSTime < 0) {
            JOptionPane.showMessageDialog(null, "Please complete all the fields with valid numbers!");
            return false;
        }
        else if(maxATime < minATime) {
            JOptionPane.showMessageDialog(null, "Wrong input: maximum arrival time greater than minimum arrival time!");
            return false;
        }
        else if(maxSTime < minSTime) {
            JOptionPane.showMessageDialog(null, "Wrong input: maximum service time greater than minimum service time!");
            return false;

        }
        else if(simTime < maxATime) {
            JOptionPane.showMessageDialog(null, "Wrong input: maximum arrival time greater than simulation time!");
            return false;
        }
        else if (simTime < maxSTime) {
            JOptionPane.showMessageDialog(null, "Wrong input: maximum service time greater than simulation time!");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SimulationManager gen = new SimulationManager();
        //System.out.println(gen.averageServiceTime);
        //Thread t = new Thread(gen);
        //t.start();
        //System.out.println(gen);
    }


    public int getPeakHour() {
        return peakHour;
    }

    public void setPeakHour(int peakHour) {
        this.peakHour = peakHour;
    }
}
