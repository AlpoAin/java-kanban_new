package tasks.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import tasks.manager.TaskManager;
import tasks.model.Epic;
import tasks.model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path   = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // /epics/{id}/subtasks
        if (path.matches("/epics/\\d+/subtasks")) {
            int id = Integer.parseInt(path.split("/")[2]);
            if (!"GET".equals(method)) {
                sendNotFound(exchange);
            } else {
                List<Subtask> list = manager.getEpicSubtasks(id);
                if (list == null) sendNotFound(exchange);
                else sendJson(exchange, gson.toJson(list));
            }
            return;
        }

        // /epics/{id}
        if (path.matches("/epics/\\d+")) {
            int id = Integer.parseInt(path.substring("/epics/".length()));
            if ("GET".equals(method)) {
                Epic e = manager.getEpic(id);
                if (e == null) sendNotFound(exchange);
                else sendJson(exchange, gson.toJson(e));
            } else if ("DELETE".equals(method)) {
                if (manager.getEpic(id) == null) {
                    sendNotFound(exchange);
                } else {
                    manager.removeEpic(id);
                    sendJson(exchange, "");
                }
            } else {
                sendNotFound(exchange);
            }
            return;
        }

        // /epics
        switch (method) {
            case "GET":
                List<Epic> all = manager.getEpics();
                sendJson(exchange, gson.toJson(all));
                break;
            case "POST":
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                try {
                    manager.addEpic(epic);
                    sendCreated(exchange);
                } catch (Exception e) {
                    sendServerError(exchange);
                }
                break;
            case "DELETE":
                manager.getEpics().forEach(e -> manager.removeEpic(e.getId()));
                sendJson(exchange, "");
                break;
            default:
                sendNotFound(exchange);
        }
    }
}
