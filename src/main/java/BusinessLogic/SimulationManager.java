package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import javax.swing.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class SimulationManager implements Runnable {
    public int timeLimit = 200;
    public int maxServiceTime = 9;
    public int minServiceTime = 3;
    public int maxArrivalTime = 100;
    public int minArrivalTime = 10;
    public int numberOfServers = 20;
    public int numberOfClients = 50;

    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;

    private BufferedWriter logWriter;

    public SimulationManager() {
        scheduler = new Scheduler(numberOfServers, numberOfClients);
        scheduler.changeStrategy(selectionPolicy);
        try {
            logWriter = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            System.out.println("Error writing log file");
            e.printStackTrace();
        }
        frame = new SimulationFrame("Simulation Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        generatedTasks = generateNRandomTasks();
        //generatedTasks = generateExampleTasks();
    }

    private List<Task> generateNRandomTasks() {
        List<Task> tasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            tasks.add(new Task(i + 1, arrivalTime, serviceTime));
        }
        System.out.println(tasks);
        tasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        System.out.println(tasks);
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
        int currentTime = 0;
        while(currentTime < timeLimit) {
            Iterator<Task> iterator = generatedTasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                    iterator.remove();
                }
            }

            log("Time " + currentTime);
            log("Waiting clients: " + formatWaitingClients(generatedTasks));

            int index = 1;
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
        //log("Average waiting time: " + calculateAverageWaitingTime());
        log("The simulation is finished when there are no more clients...");
        try {
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Simulation finished");

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

    public static void main(String[] args) {
        SimulationManager gen = new SimulationManager();
        Thread t = new Thread(gen);
        t.start();
        System.out.println(gen);
    }


}
