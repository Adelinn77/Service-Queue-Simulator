package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task task) {
        int minimumSize = Integer.MAX_VALUE;
        Server minimumServer = null;
        for(Server server : servers) {
            if(server.getTasks().size() < minimumSize){
                minimumSize = server.getWaitingPeriod().get();
                minimumServer = server;
            }
        }
        if(task != null && minimumServer != null){
            minimumServer.addTask(task);
        }
    }
}
