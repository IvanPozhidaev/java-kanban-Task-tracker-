package managers;

import tasks.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    //методы на создание задачи\эпика\подзадачи
    Task createTask(Task task);

    EpicTask createEpicTask(EpicTask epicTask);

    Subtask createSubTask(Subtask subtask);

    // методы на удаление всех задач\эпиков\подзадач
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    // метод на удаление всех сабтасков одного эпика
    void deleteAllSubtasksByEpic(EpicTask epic);

    // методы на получение всех задач\эпиков\подзадач (сначала написал с void, в итоге переделаны на возвращение)
    List<Task> getListTasks();

    List<Subtask> getListSubtasks();

    List<EpicTask> getListEpicTasks();

    // методы на получение задачи\эпика\подзадачи по идентификатору
    Task getTaskById(int id);

    EpicTask getEpicTaskById(int id);

    Subtask getSubtaskById(int id);

    ArrayList<Subtask> getSubtaskByEpicId(int id);

    // методы на обновление задачи\эпика\подзадачи
    void updateTask(Task task);

    void updateEpicTask(EpicTask epicTask);

    void updateSubTask(Subtask subtask);

    //метод на обновление статуса эпика
    void updateEpicStatus(EpicTask epicTask);

    // методы на удаление задачи\эпика\подзадачи по идентификатору
    void deleteTaskById(int id);

    void deleteEpicTaskById(int id);

    void deleteSubtaskById(int id);

    //метод на получение всех сабтасков одного эпика
    List<Subtask> getAllSubtasksByEpicId(int id);

    //метод для просмотра истории задач
    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}
