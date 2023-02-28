package server;

import com.google.gson.*;
import managers.FileBackedTasksManager;
import managers.HistoryManager;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;

    //gson со своим адаптером
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    public HttpTaskManager(HistoryManager historyManager, String path) throws IOException, InterruptedException {
        super(historyManager);
        kvTaskClient = new KVTaskClient(path);

        //парсим таски
        JsonElement jsonTasks = JsonParser.parseString(kvTaskClient.load("tasks"));
        if(!jsonTasks.isJsonNull()) {
            JsonArray jsonTasksArray = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonTasksArray) {
                Task task = gson.fromJson(jsonTask, Task.class);
                this.createTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(kvTaskClient.load("epics"));
        if(!jsonEpics.isJsonNull()) {
            JsonArray jsonEpicsArray = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonEpicsArray) {
                EpicTask epicTask = gson.fromJson(jsonEpic, EpicTask.class);
                this.createEpicTask(epicTask);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(kvTaskClient.load("subtasks"));
        if(!jsonSubtasks.isJsonNull()) {
            JsonArray jsonSubtasksArray = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonSubtasksArray) {
                Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
                this.createSubTask(subtask);
            }
        }

        JsonElement jsonHistoryList = JsonParser.parseString(kvTaskClient.load("history"));
        if (!jsonHistoryList.isJsonNull()) {
            JsonArray jsonHistoryArray = jsonHistoryList.getAsJsonArray();
            for (JsonElement jsonTaskId : jsonHistoryArray) {
                int id = jsonTaskId.getAsInt();
                if(this.subtaskMap.containsKey(id)) {
                    this.getSubtaskById(id);
                } else if (this.epicTaskMap.containsKey(id)) {
                    this.getEpicTaskById(id);
                } else if (this.taskMap.containsKey(id)) {
                    this.getTaskById(id);
                }
            }
        }

    }

    public void save() {
        kvTaskClient.put("tasks", gson.toJson(taskMap.values()));
        kvTaskClient.put("subtasks", gson.toJson(subtaskMap.values()));
        kvTaskClient.put("epics", gson.toJson(epicTaskMap.values()));
        kvTaskClient.put("history", gson.toJson(this.getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList())));
    }
}
