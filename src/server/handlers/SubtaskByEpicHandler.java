package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import server.InstantAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class SubtaskByEpicHandler implements HttpHandler {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private static final Charset STD_CHARSET = StandardCharsets.UTF_8;
    private final TaskManager taskManager;

    public SubtaskByEpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int statusCode = 400;
        String response;
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                String query = exchange.getRequestURI().getQuery();
                try {
                    int id = Integer.parseInt(query.substring(query.indexOf("id=") + 3));
                    statusCode = 200;
                    response = gson.toJson(taskManager.getSubtaskById(id));
                } catch (StringIndexOutOfBoundsException e) {
                    response = "Отсутствует параметр id в запросе.";
                } catch (NumberFormatException e) {
                    response = "Неверный формат параметра id в запросе.";
                }
                break;
            default:
                response = "Неправильный запрос.";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=" + STD_CHARSET);
        exchange.sendResponseHeaders(statusCode, 0);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
