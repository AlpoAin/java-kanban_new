package tasks.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import tasks.manager.TaskManager;
import tasks.model.Epic;

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
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                List<Epic> epics = manager.getEpics();
                sendJson(exchange, gson.toJson(epics));
                break;
            case "POST":
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                try {
                    if (epic.getId() == 0) manager.addEpic(epic);
                    else manager.addEpic(epic);
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
