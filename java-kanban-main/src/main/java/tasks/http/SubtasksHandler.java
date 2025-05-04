package tasks.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import tasks.manager.TaskManager;
import tasks.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // /subtasks/{id}
        if (path.matches("/subtasks/\\d+")) {
            int id = Integer.parseInt(path.substring("/subtasks/".length()));
            if ("GET".equals(method)) {
                Subtask s = manager.getSubtask(id);
                if (s == null) sendNotFound(exchange);
                else sendJson(exchange, gson.toJson(s));
            } else if ("DELETE".equals(method)) {
                if (manager.getSubtask(id) == null) {
                    sendNotFound(exchange);
                } else {
                    manager.removeSubtask(id);
                    sendJson(exchange, "");
                }
            } else {
                sendNotFound(exchange);
            }
            return;
        }

        // /subtasks
        switch (method) {
            case "GET":
                List<Subtask> subs = manager.getSubtasks();
                sendJson(exchange, gson.toJson(subs));
                break;
            case "POST":
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Subtask sub = gson.fromJson(body, Subtask.class);
                try {
                    manager.addSubtask(sub);
                    sendCreated(exchange);
                } catch (IllegalArgumentException e) {
                    sendNotAcceptable(exchange);
                } catch (Exception e) {
                    sendServerError(exchange);
                }
                break;
            case "DELETE":
                manager.getSubtasks().forEach(s -> manager.removeSubtask(s.getId()));
                sendJson(exchange, "");
                break;
            default:
                sendNotFound(exchange);
        }
    }
}
