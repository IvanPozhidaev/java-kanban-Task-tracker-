package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import server.InstantAdapter;
import tasks.EpicTask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class EpicHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private static final Charset STD_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;
        String method = exchange.getRequestMethod();

        //дописать реализацию
        switch(method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if(query == null) {
                    statusCode = 200;
                    String jsonString = gson.toJson(taskManager.getListEpicTasks());
                    System.out.println("GET-запрос на получение всех задач типа эпик: " + jsonString);
                    response = jsonString;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        EpicTask epic = taskManager.getEpicTaskById(id);
                        if (epic != null) {
                            response = gson.toJson(epic);
                        } else {
                            response = "Задача типа эпик не найдена.";
                        }
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "Отсутствует параметр id в запросе.";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат параметра id в запросе.";
                    }
                }
                break;
            case "POST":
                String bodyRequest = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                try {
                    EpicTask epic = gson.fromJson(bodyRequest, EpicTask.class);
                    int id = epic.getId();
                    if(taskManager.getEpicTaskById(id) != null) {
                        taskManager.updateEpicTask(epic);
                        statusCode = 200;
                        response = "Задача типа эпик обновлена.";
                    } else {
                        EpicTask epicTask = taskManager.createEpicTask(epic);
                        int epicId = epicTask.getId();
                        statusCode = 201;
                        response = "Задача типа эпик содана с id " + epicId;
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = 400;
                    response = "Ошибка в формате запроса.";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if(query == null) {
                    taskManager.deleteAllEpics();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteEpicTaskById(id);
                        statusCode = 200;
                    } catch (StringIndexOutOfBoundsException e) {
                        statusCode = 400;
                        response = "Отсутствует параметр id в запросе.";
                    } catch (NumberFormatException e) {
                        statusCode = 400;
                        response = "Неверный формат параметра id в запросе.";
                    }
                }
                break;
            default:
                statusCode = 400;
                response = "Неправильный запрос";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + STD_CHARSET);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
