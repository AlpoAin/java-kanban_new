package tasks.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;
import tasks.manager.TaskManager;
import tasks.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public TasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println(">>> TasksHandler got: " + exchange.getRequestMethod()
                + " " + exchange.getRequestURI());
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                // GET /tasks — вернуть список
                List<Task> tasks = manager.getTasks();
                String json = gson.toJson(tasks);
                sendJson(exchange, json);
                break;

            case "POST":
                // POST /tasks — создать или обновить
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);

                try {
                    if (task.getId() == 0) {
                        manager.addTask(task);
                        sendCreated(exchange);
                    } else {
                        manager.addTask(task); // у нас метод комбинированный: новый или update
                        sendCreated(exchange);
                    }
                } catch (IllegalArgumentException e) {
                    sendNotAcceptable(exchange);
                } catch (Exception e) {
                    sendServerError(exchange);
                }
                break;

            case "DELETE":
                // DELETE /tasks — удалить все
                manager.getTasks().forEach(t -> manager.removeTask(t.getId()));
                sendJson(exchange, ""); // можно 200 без тела
                break;

            default:
                sendNotFound(exchange);
        }
    }
}
