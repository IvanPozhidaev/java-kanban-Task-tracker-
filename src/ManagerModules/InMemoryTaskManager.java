package ManagerModules;

import StatusModules.Status;
import TaskModules.EpicTask;
import TaskModules.Subtask;
import TaskModules.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    int id = 1;
    // делаю приватными и финальными, чтобы повысить безопасность и менять только состояние
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //keygen
    public int idUniqueGenerator() {
        return id++;
    }

    // Метод для обновления статуса эпика
    @Override
    public void updateEpicStatus(EpicTask epicTask) {

        ArrayList<Integer> subtaskListIds = epicTask.getSubTasksIds();
        if (subtaskListIds.isEmpty()) {
            epicTask.setTaskStatus(String.valueOf(Status.NEW));
            return;
        }
        int statusDoneNumber = 0;
        int statusNewNumber = 0;
        for (int numId : subtaskListIds) {
            Subtask subtaskList = subtaskMap.get(numId);
            if (subtaskList.getTaskStatus().equals(String.valueOf(Status.DONE))) {
                statusDoneNumber+=statusDoneNumber;
                if (statusDoneNumber == subtaskListIds.size()) {
                    epicTask.setTaskStatus(String.valueOf(Status.DONE));
                    return;
                }
            } else if (subtaskList.getTaskStatus().equals(String.valueOf(Status.NEW))) {
                statusNewNumber+=statusNewNumber;
                if (statusNewNumber == subtaskListIds.size()) {
                    epicTask.setTaskStatus(String.valueOf(Status.NEW));
                    return;
                }
            } else {
                epicTask.setTaskStatus(String.valueOf(Status.IN_PROGRESS));
                return;
            }
        }
    }

    // методы на получение всех задач\эпиков\подзадач
    @Override
    public ArrayList<Task> getListTasks() {
        ArrayList<Task> listTasks = new ArrayList<>();
        for (int key : taskMap.keySet()) {
            Task task = taskMap.get(key);
            listTasks.add(task);
        }
        return listTasks;
    }

    @Override
    public ArrayList<Subtask> getListSubtasks() {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (int key : subtaskMap.keySet()) {
            Subtask subtask = subtaskMap.get(key);
            listSubtasks.add(subtask);
        }
        return listSubtasks;
    }

    @Override
    public ArrayList<EpicTask> getListEpicTasks() {
        ArrayList<EpicTask> listEpicTasks = new ArrayList<>();
        for (int key : epicTaskMap.keySet()) {
            EpicTask epicTask = epicTaskMap.get(key);
            listEpicTasks.add(epicTask);
        }
        return listEpicTasks;
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
    }

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    @Override
    public void deleteEpicTaskById(int id) {
        EpicTask epicTask = epicTaskMap.get(id);
        for(Integer subtaskId : epicTask.subTasksIds) {
            subtaskMap.remove(subtaskId);
        }
        epicTaskMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        subtaskMap.remove(id);
    }

    //методы на создание задачи\эпика\подзадачи (переделаны с учётом обновлённой структуры - теперь все методы)
    //возвращают int
    @Override
    public int createTask(Task task) {
        task.setId(idUniqueGenerator());
        task.setTaskStatus(String.valueOf(Status.NEW));
        taskMap.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int createEpicTask(EpicTask epicTask) {
        epicTask.setId(idUniqueGenerator());
        epicTask.setTaskStatus(String.valueOf(Status.NEW));
        epicTaskMap.put(epicTask.getId(), epicTask);
        return epicTask.getId();
    }

    /* в данном методе не только создаю подзадачи, но и привязываю
        их к соответствующему эпику, затем обновляю его статус через ранее прописанный метод */
    @Override
    public int createSubTask(Subtask subtask) {
        EpicTask epicTask = epicTaskMap.get(subtask.getEpicTaskId());
        subtask.setId(idUniqueGenerator());
        subtask.setTaskStatus(String.valueOf(Status.NEW));
        subtaskMap.put(subtask.getId(), subtask);
        epicTask.addSubtask(subtask.getId());
        updateEpicStatus(epicTask);
        return subtask.getId();
    }

    // методы на обновление задачи\эпика\подзадачи
    @Override
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId()) == false) {
            return;
        }
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (epicTaskMap.containsKey(epicTask.getId()) == false) {
            return;
        }
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (subtaskMap.containsKey(subtask.getId()) == false) {
            return;
        }
        int subId = subtask.getEpicTaskId();
        EpicTask epicTask = epicTaskMap.get(subId);
        if (epicTaskMap.containsKey(epicTask.getId()) == false) {
            return;
        } else {
            subtaskMap.put(subtask.getId(), subtask);
            updateEpicStatus(epicTask);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                ", historyManager=" + historyManager +
                '}';
    }
}
