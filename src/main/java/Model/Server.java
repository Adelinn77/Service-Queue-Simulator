package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private volatile boolean running = true;

    public Server() {
        tasks = new LinkedBlockingQueue<>();
        waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setWaitingTime(waitingPeriod.get());
        waitingPeriod.getAndAdd(task.getServiceTime());
        System.out.println("Adăugat task " + task.getID() + " la server.");
    }

    @Override
    public void run() {
        while(running) {
            try {
                Task task = tasks.peek();
                if (task != null) {
                    if(task.isPoisonPill()) {
                        break;
                    }
                    System.out.println("Server a preluat task " + task.getID());
                    int currentServiceTime = task.getServiceTime();

                    while (currentServiceTime > 0) {
                        Thread.sleep(1000);
                        currentServiceTime--;
                        task.setServiceTime(currentServiceTime);
                        waitingPeriod.addAndGet(-1);
                    }
                    tasks.poll();
                }


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

    public void stop() {
        this.running = false;
    }
}
