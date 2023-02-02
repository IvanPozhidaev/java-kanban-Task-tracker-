package ManagerModules;

import StatusModules.*;
import TaskModules.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    int id = 1;
    // делаю приватными и финальными, чтобы повысить безопасность и менять только состояние
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager(){}
    //keygen
    public int idUniqueGenerator() {
        return ++id;
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // Метод для обновления статуса эпика
    @Override
    public void updateEpicStatus(EpicTask epicTask) {

        ArrayList<Integer> subtaskListIds = epicTask.getSubTasksIds();
        if (subtaskListIds.isEmpty()) {
            epicTask.setTaskStatus(Status.NEW);
            return;
        }
        int statusDoneNumber = 0;
        int statusNewNumber = 0;
        for (int numId : subtaskListIds) {
            Subtask subtaskList = subtaskMap.get(numId);
            if (subtaskList.getTaskStatus().equals(Status.DONE)) {
                statusDoneNumber+=statusDoneNumber;
                if (statusDoneNumber == subtaskListIds.size()) {
                    epicTask.setTaskStatus(Status.DONE);
                    return;
                }
            } else if (subtaskList.getTaskStatus().equals(Status.NEW)) {
                statusNewNumber+=statusNewNumber;
                if (statusNewNumber == subtaskListIds.size()) {
                    epicTask.setTaskStatus(Status.NEW);
                    return;
                }
            } else {
                epicTask.setTaskStatus(Status.IN_PROGRESS);
                return;
            }
        }
    }

    // методы на получение всех задач\эпиков\подзадач обновлены на более лаконичную запись
    @Override
    public List<Task> getListTasks() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Subtask> getListSubtasks() {
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public List<EpicTask> getListEpicTasks() {
        return new ArrayList<>(epicTaskMap.values());
    }

    // методы на удаление всех задач\эпиков\подзадач
    @Override
    public void deleteAllTasks() {
        taskMap.clear();
    }

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    @Override
    public void deleteAllEpics() {
        epicTaskMap.clear();
        subtaskMap.clear();
    }

    //здесь отдельный метод для удаления субзадач, потому что эпики могут существовать без подзадач
    @Override
    public void deleteAllSubtasks() {
        subtaskMap.clear();
    }

    // методы на получение задачи\эпика\подзадачи по идентификатору (обновлены с учётом ведения истории запросов)
    @Override
    public Task getTaskById(int id) {
        historyManager.add(taskMap.get(id));
        return taskMap.get(id);
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        historyManager.add(epicTaskMap.get(id));
        return epicTaskMap.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtaskMap.get(id));
        return subtaskMap.get(id);
    }

    @Override
    public ArrayList<Subtask> getSubtaskByEpicId(int id) {
        EpicTask epicTask = epicTaskMap.get(id);
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        for (int subtask : epicTask.getSubTasksIds()) {
            listSubtask.add(subtaskMap.get(subtask));
        }
        return listSubtask;
    }

    // методы на удаление задачи\эпика\подзадачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        taskMap.remove(id);
        historyManager.remove(id);
    }

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    @Override
    public void deleteEpicTaskById(int id) {
        if (epicTaskMap.containsKey(id)) {
            EpicTask epicTask = epicTaskMap.get(id);
            for (Integer subtaskId : epicTask.subTasksIds) {
                subtaskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epicTaskMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id)    {
        subtaskMap.remove(id);
        historyManager.remove(id);
    }

    //методы на создание задачи\эпика\подзадачи (переделаны с учётом обновлённой структуры - теперь все методы)
    //возвращают int
    @Override
    public int createTask(Task task) {
        task.setId(idUniqueGenerator());
        task.setTaskStatus(Status.NEW);
        taskMap.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpicTask(EpicTask epicTask) {
        epicTask.setId(idUniqueGenerator());
        epicTask.setTaskStatus(Status.NEW);
        epicTaskMap.put(epicTask.getId(), epicTask);
        return epicTask.getId();
    }

    /* в данном методе не только создаю подзадачи, но и привязываю
        их к соответствующему эпику, затем обновляю его статус через ранее прописанный метод */
    @Override
    public int createSubTask(Subtask subtask) {
        EpicTask epicTask = epicTaskMap.get(subtask.getEpicTaskId());
        subtask.setId(idUniqueGenerator());
        subtask.setTaskStatus(Status.NEW);
        subtaskMap.put(subtask.getId(), subtask);
        epicTask.addSubtask(subtask.getId());
        updateEpicStatus(epicTask);
        return subtask.getId();
    }

    // методы на обновление задачи\эпика\подзадачи
    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (epicTaskMap.containsKey(epicTask.getId())) {
            epicTaskMap.put(epicTask.getId(), epicTask);
        }
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subtaskMap.containsKey(subtask.getId())) {
            EpicTask epicTask = epicTaskMap.get(subtask.getEpicTaskId());
            if (epicTaskMap.containsKey(epicTask.getId())) {
                subtaskMap.put(subtask.getId(), subtask);
                updateEpicStatus(epicTask);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                ", historyManager=" + historyManager +
                '}';
    }

    public void addToHistory(int id) {
        if (epicTaskMap.containsKey(id)) {
            historyManager.add(epicTaskMap.get(id));
        } else if (subtaskMap.containsKey(id)) {
            historyManager.add(subtaskMap.get(id));
        } else if (taskMap.containsKey(id)) {
            historyManager.add(taskMap.get(id));
        }
    }
}
