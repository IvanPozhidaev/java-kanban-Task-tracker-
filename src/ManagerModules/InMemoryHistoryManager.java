package ManagerModules;

import TaskModules.Task;
import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    //двусвязный список для удобства (+ удобные внутренние методы)
    private final LinkedList<Task> tasksHistory = new LinkedList<>();
    // финальная статичная переменная для установления максимального количества записей списка
    private static final int LIST_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }

    @Override
    public void add(Task task) {
        tasksHistory.add(task);
        if (tasksHistory.size() > LIST_SIZE) {
            tasksHistory.removeFirst();
        }
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "tasksHistory=" + tasksHistory +
                '}';
    }
}
