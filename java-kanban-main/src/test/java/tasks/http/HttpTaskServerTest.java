package tasks.http;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.manager.InMemoryTaskManager;
import tasks.manager.TaskManager;
import tasks.model.Task;
import tasks.model.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        // новый чистый менеджер и сервер
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();

        client = HttpClient.newHttpClient();
        gson = taskServer.getGson();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void testGetEmptyTasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }

    @Test
    void testPostAndGetTask() throws Exception {
        // создаём задачу
        Task task = new Task("Test task", "Описание");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.of(2025, 1, 1, 9, 0));

        String json = gson.toJson(task);

        // POST /tasks
        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> postResp = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResp.statusCode());

        // GET /tasks
        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> getResp = client.send(get, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResp.statusCode());

        // парсим обратно
        List<Task> tasks = List.of(gson.fromJson(getResp.body(), Task[].class));
        assertEquals(1, tasks.size());

        Task saved = tasks.get(0);
        assertEquals("Test task", saved.getName());
        assertEquals("Описание", saved.getDescription());
        assertEquals(Status.NEW, saved.getStatus());
        assertEquals(Duration.ofMinutes(30), saved.getDuration());
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 0), saved.getStartTime());
    }

    @Test
    void testDeleteAllTasks() throws Exception {
        // создаём две задачи
        for (int i = 0; i < 2; i++) {
            Task t = new Task("T" + i, "D" + i);
            t.setStartTime(LocalDateTime.now());
            t.setDuration(Duration.ofMinutes(5));
            String json = gson.toJson(t);
            client.send(HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:8080/tasks"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json))
                            .build(),
                    HttpResponse.BodyHandlers.discarding());
        }

        // DELETE /tasks
        HttpResponse<String> delResp = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .DELETE()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, delResp.statusCode());

        // теперь GET /tasks снова
        HttpResponse<String> after = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, after.statusCode());
        assertEquals("[]", after.body());
    }
}
