package tasks.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import tasks.manager.Managers;
import tasks.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;

        this.gson = new GsonBuilder()
                .serializeNulls()

                // Duration ↔ минуты
                .registerTypeAdapter(Duration.class, (JsonSerializer<Duration>) (src, type, ctx) ->
                        src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toMinutes()))
                .registerTypeAdapter(Duration.class, (JsonDeserializer<Duration>) (json, type, ctx) ->
                        json == null || json.isJsonNull() ? null : Duration.ofMinutes(json.getAsLong()))

                // LocalDateTime ↔ ISO-строка
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                        src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                        json == null || json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString()))

                .create();

        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks",       new TasksHandler(manager, gson));
        server.createContext("/subtasks",    new SubtasksHandler(manager, gson));
        server.createContext("/epics",       new EpicsHandler(manager, gson));
        server.createContext("/history",     new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HTTP Task Server started on port " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP Task Server stopped");
    }

    public static void main(String[] args) throws IOException {
        TaskManager mgr = Managers.getDefault();
        HttpTaskServer httpServer = new HttpTaskServer(mgr);
        httpServer.start();
    }

    public Gson getGson() {
        return gson;
    }
}
