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
        String method = exchange.getRequestMethod();
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
                    if (sub.getId() == 0) manager.addSubtask(sub);
                    else manager.addSubtask(sub);
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
