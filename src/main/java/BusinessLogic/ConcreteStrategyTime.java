package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task task) {
        int minimumTime = Integer.MAX_VALUE;
        Server minimumServer = null;
        for(Server server : servers) {
            if(server.getWaitingPeriod().get() < minimumTime){
                minimumTime = server.getWaitingPeriod().get();
                minimumServer = server;
            }
        }
        if(task != null && minimumServer != null){
            minimumServer.addTask(task);
        }
    }
}
