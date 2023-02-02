package ManagerModules;

public class Managers {

    public static HistoryManager getDefaultHistory () {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static FileBackedTasksManager getDefaultFileManager() {
        return new FileBackedTasksManager();
    }

}
