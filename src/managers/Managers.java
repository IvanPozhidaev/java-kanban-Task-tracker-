package managers;

import server.HttpTaskManager;
import server.KVServer;

import java.io.IOException;

public class Managers {

    public static HistoryManager getDefaultHistory () {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getInMemoryTaskManager(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HttpTaskManager getDefault(HistoryManager historyManager) throws IOException, InterruptedException {
        return new HttpTaskManager(historyManager, "http://localhost:" + KVServer.PORT);
    }
}
