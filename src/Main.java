import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.FileBackedTasksManager;
import managers.HistoryManager;
import managers.Managers;
import enums.Status;
import managers.TaskManager;
import server.InstantAdapter;
import server.KVServer;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer kvServer;

        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantAdapter())
                    .create();

            kvServer = new KVServer();
            kvServer.start();

            HistoryManager historyManager = Managers.getDefaultHistory();
            TaskManager taskManager = Managers.getDefault(historyManager);

            Task task1 = new Task("Title Task 1", "Description Task 1",
                    Status.NEW, Instant.now(), 15);
            taskManager.createTask(task1);
            Task task2 = new Task("Title Task 2", "Description Task 2",
                    Status.NEW, Instant.now(), 15);
            taskManager.createTask(task2);

            EpicTask epic = new EpicTask("Title Epic 1", "Description Epic 1",
                    Status.NEW, Instant.now(), 45);
            taskManager.createEpicTask(epic);

            Subtask subtask1 = new Subtask("Title Subtask 1", "Description Subtask 1",
                    Status.NEW, epic.getId(), Instant.now(), 15);
            taskManager.createSubTask(subtask1);
            Subtask subtask2 = new Subtask("Title Subtask 2", "Description Subtask 2",
                    Status.NEW, epic.getId(), Instant.now(), 15);
            taskManager.createSubTask(subtask2);
            Subtask subtask3 = new Subtask("Title Subtask 3", "Description Subtask 3",
                    Status.NEW, epic.getId(), Instant.now(), 15);
            taskManager.createSubTask(subtask3);

            taskManager.getTaskById(task1.getId());
            taskManager.getTaskById(task2.getId());
            taskManager.getEpicTaskById(epic.getId());
            taskManager.getSubtaskById(subtask1.getId());
            taskManager.getSubtaskById(subtask2.getId());
            taskManager.getSubtaskById(subtask3.getId());

            System.out.println(gson.toJson(taskManager.getListTasks()));
            System.out.println(gson.toJson(taskManager.getListEpicTasks()));
            System.out.println(gson.toJson(taskManager.getListSubtasks()));
            System.out.println(taskManager);

            kvServer.stop();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
