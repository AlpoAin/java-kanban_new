package tasks.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import tasks.manager.TaskManager;
import tasks.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // 1) /tasks/{id}
        if (path.matches("/tasks/\\d+")) {
            int id = Integer.parseInt(path.substring("/tasks/".length()));
            if ("GET".equals(method)) {
                Task t = manager.getTask(id);
                if (t == null) sendNotFound(exchange);
                else sendJson(exchange, gson.toJson(t));
            } else if ("DELETE".equals(method)) {
                if (manager.getTask(id) == null) {
                    sendNotFound(exchange);
                } else {
                    manager.removeTask(id);
                    sendJson(exchange, "");
                }
            } else {
                sendNotFound(exchange);
            }
            return;
        }

        // 2) /tasks
        switch (method) {
            case "GET": {
                List<Task> all = manager.getTasks();
                sendJson(exchange, gson.toJson(all));
                break;
            }
            case "POST": {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                try {
                    manager.addTask(task);
                    sendCreated(exchange);
                } catch (IllegalArgumentException e) {
                    sendNotAcceptable(exchange);
                } catch (Exception e) {
                    sendServerError(exchange);
                }
                break;
            }
            case "DELETE": {
                manager.getTasks().forEach(t -> manager.removeTask(t.getId()));
                sendJson(exchange, "");
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }
}
