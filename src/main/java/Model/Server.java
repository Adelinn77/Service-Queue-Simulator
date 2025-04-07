package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server() {
        tasks = new LinkedBlockingQueue<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void run() {
        while(true) {

        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }
}
