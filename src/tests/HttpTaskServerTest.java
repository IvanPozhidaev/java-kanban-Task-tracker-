package tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import enums.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.InstantAdapter;
import server.KVServer;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer taskServer;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();
    private final String PATH_DEFAULT = "http://localhost:8080/";

    @BeforeAll
    static void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            taskServer = new HttpTaskServer();
            taskServer.start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {
        kvServer.stop();
        taskServer.stop();
    }

    @BeforeEach
    void resetServer() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/task/");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(PATH_DEFAULT + "tasks/epic/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            url = URI.create(PATH_DEFAULT + "tasks/subtask/");
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/task/");
        Task task = new Task("Title Task 1", "Description Task 1", Status.NEW,
                Instant.now(), 15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            System.out.println("response.body(): " + response.body());
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, array.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicsTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            System.out.println("response.body(): " + response.body());
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, array.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST-запрос");
            if (postResponse.statusCode() == 201) {
                System.out.println("postResponse.body(): " + postResponse.body());
                int epicId = Integer.parseInt(postResponse.body().split(" ")[6]);
                epic.setId(epicId);
                Subtask subtask = new Subtask("Title Subtask 1", "Description Subtask 1", Status.NEW,
                        epic.getId(), Instant.now(), 4);
                url = URI.create(PATH_DEFAULT + "tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(1, array.size());
        }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/task/");
        Task task = new Task("Title Task 1", "Description Task 1", Status.NEW,
                Instant.now(), 15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST-запрос");
            if(postResponse.statusCode() == 201) {
                System.out.println("postResponse.body(): " + postResponse.body());
                int id = Integer.parseInt(postResponse.body().split(" ")[4]);
                task.setId(id);
                url = URI.create(PATH_DEFAULT + "tasks/task/" + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                Task responseTask = gson.fromJson(response.body(), Task.class);
                assertEquals(task.getId(), responseTask.getId());
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getEpicTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST-запрос");
            if(postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body().split(" ")[6]);
                epic.setId(id);
                url = URI.create(PATH_DEFAULT + "tasks/epic/" + "?id=" + id);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                EpicTask responseEpicTask = gson.fromJson(response.body(), EpicTask.class);
                assertEquals(epic.getId(), responseEpicTask.getId());
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getSubtaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST-запрос");
            if(postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body().split(" ")[6]);
                epic.setId(epicId);
                Subtask subtask = new Subtask("Title Epic 1", "Description Task 1", Status.NEW,
                        epic.getId(), Instant.now(), 4);
                url = URI.create(PATH_DEFAULT + "tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(201, postResponse.statusCode(), "POST-запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body().split(" ")[4]);
                    subtask.setId(id);
                    url = URI.create(PATH_DEFAULT + "tasks/subtask/" + "?id=" + id);
                    request = HttpRequest.newBuilder()
                            .uri(url)
                            .GET()
                            .build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());
                    Subtask responseSubtask = gson.fromJson(response.body(), Subtask.class);
                    assertEquals(subtask.getId(), responseSubtask.getId());
                }
            }
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/task/");
        Task task = new Task("Title Task 1", "Description Task 1", Status.NEW,
                Instant.now(), 15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, array.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteEpicTasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            request = HttpRequest.newBuilder()
                    .uri(url)
                    .GET()
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
            assertEquals(0, array.size());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteSubtasksTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST-запрос");
            if (postResponse.statusCode() == 201) {
                int epicId = Integer.parseInt(postResponse.body().split(" ")[6]);
                epic.setId(epicId);

                Subtask subtask = new Subtask("Title Epic 1", "Description Task 1", Status.NEW,
                        epic.getId(), Instant.now(), 4);
                url = URI.create(PATH_DEFAULT + "tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.ofString());
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .DELETE()
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .GET()
                        .build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());
                JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
                assertEquals(0, array.size());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/task/");
        Task task = new Task("Title Task 1", "Description Task 1", Status.NEW, Instant.now(), 15);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int id = Integer.parseInt(postResponse.body().split(" ")[4]);
            System.out.println(id);
            url = URI.create(PATH_DEFAULT + "tasks/task/" + "?id=" + id);
            request = HttpRequest.newBuilder().uri(url).DELETE().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            request = HttpRequest.newBuilder().uri(url).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals("Нет задачи с этим id.", response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteEpicTaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1", Status.NEW, Instant.now(),
                15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                int id = Integer.parseInt(postResponse.body().split(" ")[6]);
                url = URI.create(PATH_DEFAULT + "tasks/epic/" + "?id=" + id);

                request = HttpRequest.newBuilder().uri(url).DELETE().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals(200, response.statusCode());

                request = HttpRequest.newBuilder().uri(url).GET().build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                assertEquals("Задача типа эпик не найдена.", response.body());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    void deleteSubtaskByIdTest() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(PATH_DEFAULT + "tasks/epic/");
        EpicTask epic = new EpicTask("Title Epic 1", "Description Task 1",
                Status.NEW, Instant.now(), 15);

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, postResponse.statusCode(), "POST запрос");
            if (postResponse.statusCode() == 201) {
                Subtask subtask = new Subtask("Title SubTask 1", "Description SubTask 1",
                        Status.NEW, epic.getId(), Instant.now(), 19);
                url = URI.create(PATH_DEFAULT + "tasks/subtask/");

                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                        .build();
                postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

                assertEquals(201, postResponse.statusCode(), "POST запрос");
                if (postResponse.statusCode() == 201) {
                    int id = Integer.parseInt(postResponse.body().split(" ")[4]);
                    subtask.setId(id);

                    url = URI.create(PATH_DEFAULT + "tasks/subtask/" + "?id=" + id);
                    request = HttpRequest.newBuilder().uri(url).DELETE().build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals(200, response.statusCode());

                    request = HttpRequest.newBuilder().uri(url).GET().build();
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    assertEquals("Подзадача не найдена.", response.body());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
