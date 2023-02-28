package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import server.InstantAdapter;
import tasks.Subtask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class SubtaskHandler implements HttpHandler {

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private static final Charset STD_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode;
        String response;
        String method = exchange.getRequestMethod();

        //дописать реализацию
        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    statusCode = 200;
                    response = gson.toJson(taskManager.getListSubtasks());
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        Subtask subtask = taskManager.getSubtaskById(id);
                        if (subtask != null) {
                            response = gson.toJson(subtask);
                        } else {
                            response = "Подзадача не найдена.";
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
                String bodyRequest = new String (exchange.getRequestBody().readAllBytes(), STD_CHARSET);
                try {
                    Subtask subtask = gson.fromJson(bodyRequest, Subtask.class);
                    int id = subtask.getId();
                    if (taskManager.getSubtaskById(id) != null) {
                        taskManager.updateSubTask(subtask);
                        statusCode = 200;
                        response = "Подзадача обновлена";
                    } else {
                        Subtask newSubtask = taskManager.createSubTask(subtask);
                        int idSub = newSubtask.getId();
                        statusCode = 201;
                        response = "Подзадача содана с id " + idSub;
                    }
                } catch (JsonSyntaxException e) {
                    statusCode = 400;
                    response = "Ошибка в формате запроса.";
                }
                break;
            case "DELETE":
                response = "";
                query = exchange.getRequestURI().getQuery();
                if (query == null) {
                    taskManager.deleteAllSubtasks();
                    statusCode = 200;
                } else {
                    try {
                        int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                        taskManager.deleteSubtaskById(id);
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

        try (
                OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}