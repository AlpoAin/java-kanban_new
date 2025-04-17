package tasks.manager;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import tasks.model.Status;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private boolean isLoading = false;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    public void save() {
        if (isLoading) return;
        try (Writer writer = new FileWriter(file)) {
            // пишем заголовок
            writer.append(HEADER).append("\n");

            // пишем все Task
            for (Task t : getTasks().values()) {
                writer.append(toString(t)).append("\n");
            }
            // пишем все Epic
            for (Epic e : getEpics().values()) {
                writer.append(toString(e)).append("\n");
            }
            // пишем все Subtask
            for (Subtask s : getSubtasks().values()) {
                writer.append(toString(s)).append("\n");
            }

            // разделитель перед историей
            writer.append("\n");
            // пишем историю просмотров
            writer.append(historyToString());

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);
        }
    }

    private String toString(Task task) {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(String.valueOf(task.getId()));
        joiner.add(task.getClass().getSimpleName().toUpperCase());
        joiner.add(task.getName());
        joiner.add(task.getStatus().name());
        joiner.add(task.getDescription());
        String epicId = (task instanceof Subtask)
                ? String.valueOf(((Subtask) task).getEpicId())
                : "";
        joiner.add(epicId);
        return joiner.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.isLoading = true;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) {
                manager.isLoading = false;
                return manager;
            }
            // Пропустить заголовок, читать до пустой строки
            int i = 1;
            for (; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) break;
                Task task = fromString(line);
                switch (task.getClass().getSimpleName().toUpperCase()) {
                    case "TASK":
                        manager.addTask(task);
                        break;
                    case "EPIC":
                        manager.addEpic((Epic) task);
                        break;
                    case "SUBTASK":
                        manager.addSubtask((Subtask) task);
                        break;
                }
            }
            // Восстановление истории
            if (i + 1 < lines.size()) {
                List<Integer> historyIds = historyFromString(lines.get(i + 1));
                for (int id : historyIds) {
                    if (manager.getTasks().containsKey(id))
                        manager.getHistoryManager().add(manager.getTasks().get(id));
                    else if (manager.getEpics().containsKey(id))
                        manager.getHistoryManager().add(manager.getEpics().get(id));
                    else if (manager.getSubtasks().containsKey(id))
                        manager.getHistoryManager().add(manager.getSubtasks().get(id));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        }
        manager.isLoading = false;
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",", -1);
        int    id     = Integer.parseInt(parts[0]);
        String type   = parts[1];
        String name   = parts[2];
        Status status = Status.valueOf(parts[3]);
        String desc   = parts[4];
        String epicPart = parts.length > 5 ? parts[5] : "";
        switch (type) {
            case "TASK":
                return new Task(id, name, desc, status);
            case "EPIC":
                return new Epic(id, name, desc, status, new ArrayList<>());
            case "SUBTASK":
                int epicId = epicPart.isEmpty() ? 0 : Integer.parseInt(epicPart);
                return new Subtask(id, name, desc, status, epicId);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private String historyToString() {
        return getHistory().stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String value) {
        if (value.isEmpty()) return Collections.emptyList();
        String[] tokens = value.split(",");
        List<Integer> ids = new ArrayList<>();
        for (String t : tokens) {
            ids.add(Integer.parseInt(t));
        }
        return ids;
    }

    // Доступ к защищённым коллекциям родителя
    @Override protected Map<Integer, Task> getTasks()      { return super.getTasks(); }
    @Override protected Map<Integer, Epic> getEpics()      { return super.getEpics(); }
    @Override protected Map<Integer, Subtask> getSubtasks(){ return super.getSubtasks(); }
    @Override protected HistoryManager getHistoryManager() { return super.getHistoryManager(); }
}
