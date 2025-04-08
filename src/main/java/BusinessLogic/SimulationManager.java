package BusinessLogic;

import GUI.SimulationFrame;
import Model.Task;

import java.util.*;

public class SimulationManager implements Runnable {
    public int timeLimit = 100;
    public int maxServiceTime = 10;
    public int minServiceTime = 2;
    public int maxArrivalTime = 2;
    public int minArrivalTime = 2;
    public int numberOfServers = 3;
    public int numberOfClients = 100;
    public SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    private Scheduler scheduler;

    private SimulationFrame frame;

    private List<Task> generatedTasks;

    public SimulationManager() {
        scheduler = new Scheduler(numberOfServers, numberOfClients);
        scheduler.changeStrategy(selectionPolicy);
        frame = new SimulationFrame();
        frame.setVisible(true);
        generatedTasks = generateNRandomTasks();
    }

    private List<Task> generateNRandomTasks() {
        List<Task> tasks = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;
            tasks.add(new Task(i + 1, arrivalTime, serviceTime));
        }

        tasks.sort(Comparator.comparingInt(Task::getArrivalTime));
        return tasks;
    }


    @Override
    public void run() {
        int currentTime = 0;
        while(currentTime < timeLimit) {
            for(Task task : generatedTasks) {
                if(task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                    generatedTasks.remove(task);
                    //update UI frame
                }
            }
            currentTime++;
        }
    }

    public static void main(String[] args) {
        SimulationManager gen = new SimulationManager();
        Thread t = new Thread(gen);
        t.start();
    }
}
