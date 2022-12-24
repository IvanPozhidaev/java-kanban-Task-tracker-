package ManagerModules;

import TaskModules.EpicTask;
import TaskModules.Subtask;
import TaskModules.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void updateEpicStatus(EpicTask epicTask);

    // методы на получение всех задач\эпиков\подзадач (сначала написал с void, в итоге переделаны на возвращение)
    ArrayList<Task> getListTasks();
    ArrayList<Subtask> getListSubtasks();

    ArrayList<EpicTask> getListEpicTasks();

    // методы на удаление всех задач\эпиков\подзадач
    void deleteAllTasks();

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    void deleteAllEpics();

    //здесь отдельный метод для удаления субзадач, потому что эпики могут существовать без подзадач
    void deleteAllSubtasks();

    // методы на получение задачи\эпика\подзадачи по идентификатору
    Task getTaskById(int id);

    EpicTask getEpicTaskById(int id);

    ArrayList<Subtask> getSubtaskByEpicId(int id);

    Subtask getSubtaskById(int id);

    // методы на удаление задачи\эпика\подзадачи по идентификатору
    void deleteTaskById(int id);

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    void deleteEpicTaskById(int id);

    void deleteSubtaskById(int id);

    //методы на создание задачи\эпика\подзадачи
    int createTask(Task task);

    int createEpicTask(EpicTask epicTask);

    /* в данном методе не только создаю подзадачи, но и привязываю
        их к соответствующему эпику, затем обновляю его статус через ранее прописанный метод */
    int createSubTask(Subtask subtask);

    // методы на обновление задачи\эпика\подзадачи
    void updateTask(Task task);

    void updateEpicTask(EpicTask epicTask);

    void updateSubTask(Subtask subtask);

    List<Task> getHistory();
}
