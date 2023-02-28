package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.*;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer() throws IOException, InterruptedException {
        //создать сервак, менеджеры и привязать к порту
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);
        this.httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

        //создать контекст для обработки каждого эндпоинта
        httpServer.createContext("/tasks/", new TasksHandler(taskManager));
        httpServer.createContext("/tasks/task/", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic/", new EpicHandler(taskManager));
        httpServer.createContext("/tasks/subtask/", new SubtaskHandler(taskManager));
        httpServer.createContext("/tasks/subtask/epic/", new SubtaskByEpicHandler(taskManager));
        httpServer.createContext("/tasks/history/", new HistoryHandler(taskManager));
    }

    //запуск сервера
    public void start() {
        httpServer.start();
    }

    //остановка сервера
    public void stop() {
        httpServer.stop(1);
    }
}
