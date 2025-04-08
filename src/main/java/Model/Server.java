package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;

    public Server() {
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task task) {
        tasks.add(task);
        waitingPeriod.getAndAdd(task.getServiceTime());
    }

    @Override
    public void run() {
        while(true) {
            try {
                Task task = tasks.take();
                int currentServiceTime = task.getServiceTime();

                while (currentServiceTime > 0) {
                    Thread.sleep(1000);

                    currentServiceTime--;
                    task.setServiceTime(currentServiceTime);

                }
                waitingPeriod.addAndGet(-task.getServiceTime());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }
}
