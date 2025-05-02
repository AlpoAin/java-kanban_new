package tasks.http;

import com.sun.net.httpserver.HttpServer;
import tasks.manager.TaskManager;
import tasks.manager.Managers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.gson = new GsonBuilder().serializeNulls().create();

        // Создаём сервер на порту 8080, без очереди (0)
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Здесь будем регистрировать контексты (эндпоинты)
        server.createContext("/tasks",      new TasksHandler(manager, gson));
        server.createContext("/subtasks",   new SubtasksHandler(manager, gson));
        server.createContext("/epics",      new EpicsHandler(manager, gson));
        server.createContext("/history",    new HistoryHandler(manager, gson));
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
        TaskManager mgr = Managers.getDefault(); // или FileBackedTaskManager.loadFromFile(...)
        HttpTaskServer httpServer = new HttpTaskServer(mgr);
        httpServer.start();
    }

    /** Чтобы из тестов брать одинаковый экземпляр Gson */
    public Gson getGson() {
        return gson;
    }
}
