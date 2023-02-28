package tests;

import enums.Status;
import managers.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskManager;
import server.KVServer;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest <HttpTaskManager>{
    private KVServer kvServer;
    HttpTaskManager httpTaskManager;

    @Override
    public HttpTaskManager createManager() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            HistoryManager historyManager = Managers.getDefaultHistory();
            httpTaskManager = Managers.getDefault(historyManager);
        } catch (IOException | InterruptedException e) {
            System.out.println("При создании менеджера возникла ошибка");
        }
        return httpTaskManager;
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void loadTasksTest() {
        Task task1 = new Task("Title Task 1", "Description Task 1", Status.NEW, Instant.now(),
                15);
        Task task2 = new Task("Title Task 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        manager.createTask(task1);
        manager.createTask(task2);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());

        List<Task> list = manager.getHistory();
        assertEquals(manager.getListTasks(), list);
    }

    @Test
    public void loadEpicTasksTest() {
        EpicTask epic1 = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);
        EpicTask epic2 = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        manager.createEpicTask(epic1);
        manager.createEpicTask(epic2);

        manager.getEpicTaskById(epic1.getId());
        manager.getEpicTaskById(epic2.getId());

        List<Task> list = manager.getHistory();
        assertEquals(manager.getListEpicTasks(), list);
    }

    @Test
    public void loadSubtasksTest() {
        EpicTask epic1 = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW,
                Instant.now(), 15);
        Subtask subtask1 = new Subtask("Title Epic 1", "Description Task 1", Status.NEW,
                epic1.getId(), Instant.now(), 15);
        Subtask subtask2 = new Subtask("Title Epic 1", "Description Task 1", Status.NEW,
                epic1.getId(), Instant.now(), 15);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        manager.getSubtaskById(subtask1.getId());
        manager.getSubtaskById(subtask2.getId());

        List<Task> list = manager.getHistory();
        assertEquals(manager.getListSubtasks(), list);
    }
}
