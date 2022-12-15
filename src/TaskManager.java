import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    int id = 1;
    Task task;
    EpicTask epicTask;
    Subtask subtask;
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    //keygen
    public int idUniqueGenerator() {
        return id++;
    }

    /*В ТЗ указано, что не должно существовать каких-то отдельных методов для обновления статуса,
    * однако я не смог ничего этакого придумать. Потому написал метод, который занимается обновлением статуса
    * эпика, и который можно универсально применить и к подазадчам тоже, поскольку по ТЗ дано, что они связаны.
    * Я надеюсь, это будет приемлемо и пройдет проверку, ибо мои идеи по реализации иссякли) */

    // Метод для обновления статуса эпика
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

    // методы на получение всех задач\эпиков\подзадач (сначала написал с void, в итоге переделаны на возвращение)
    public ArrayList<Task> getListTasks() {
        ArrayList<Task> listTasks = new ArrayList<>();
        for (int key : taskMap.keySet()) {
            Task task = taskMap.get(key);
            listTasks.add(task);
        }
        return listTasks;
    }

    public ArrayList<Subtask> getListSubtasks() {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (int key : subtaskMap.keySet()) {
            Subtask subtask = subtaskMap.get(key);
            listSubtasks.add(subtask);
        }
        return listSubtasks;
    }

    public ArrayList<EpicTask> getListEpicTasks() {
        ArrayList<EpicTask> listEpicTasks = new ArrayList<>();
        for (int key : epicTaskMap.keySet()) {
            EpicTask epicTask = epicTaskMap.get(key);
            listEpicTasks.add(epicTask);
        }
        return listEpicTasks;
    }

    // методы на удаление всех задач\эпиков\подзадач
    public void deleteAllTasks() {
        taskMap.clear();
    }

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    public void deleteAllEpics() {
        epicTaskMap.clear();
        subtaskMap.clear();
    }

    //здесь отдельный метод для удаления субзадач, потому что эпики могут существовать без подзадач
    public void deleteAllSubtasks() {
        subtaskMap.clear();
    }

    // методы на получение задачи\эпика\подзадачи по идентификатору
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        return task;
    }

    public EpicTask getEpicTaskById(int id) {
        EpicTask epicTask = epicTaskMap.get(id);
        return epicTask;
    }

    public ArrayList<Subtask> getSubtaskByEpicId(int id) {
        EpicTask epicTask = epicTaskMap.get(id);
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        for (int subtask : epicTask.getSubTasksIds()) {
            listSubtask.add(subtaskMap.get(subtask));
        }
        return listSubtask;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskMap.get(id);
        return subtask;
    }


    // методы на удаление задачи\эпика\подзадачи по идентификатору
    public void deleteTaskById(int id) {
        taskMap.remove(id);
    }

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    public void deleteEpicTaskById(int id) {
        EpicTask epicTask = epicTaskMap.get(id);
        for(Integer subtaskId : epicTask.subTasksIds) {
            subtaskMap.remove(subtaskId);
        }
        epicTaskMap.remove(id);
    }

    public void deleteSubtaskById(int id) {
        subtaskMap.remove(id);
    }

    //методы на создание задачи\эпика\подзадачи
    public void createTask(Task task) {
        task.setId(idUniqueGenerator());
        task.setTaskStatus(String.valueOf(Status.NEW));
        taskMap.put(task.getId(), task);
    }

    public void createEpicTask(EpicTask epicTask) {
        epicTask.setId(idUniqueGenerator());
        epicTask.setTaskStatus(String.valueOf(Status.NEW));
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

    /* в данном методе не только создаю подзадачи, но и привязываю
        их к соответствующему эпику, затем обновляю его статус через ранее прописанный метод */
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
    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId()) == false) {
            return;
        }
        taskMap.put(task.getId(), task);
    }

    public void updateEpicTask(EpicTask epicTask) {
        if (epicTaskMap.containsKey(epicTask.getId()) == false) {
            return;
        }
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

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


}
