package tasks.manager;

import tasks.model.Task;
import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Status;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    boolean isLoading = false;
    private static final String HEADER =
            "id,type,name,status,description,epic,duration,startTime";

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

    private String toCsvString(Task task) {
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
        // duration in minutes
        joiner.add(task.getDuration() != null
                ? String.valueOf(task.getDuration().toMinutes())
                : "");
        // startTime ISO format or empty
        joiner.add(task.getStartTime() != null
                ? task.getStartTime().toString()
                : "");
        return joiner.toString();
    }

    public void save() {
        if (isLoading) return;

        try (Writer writer = new FileWriter(file)) {
            // Заголовок
            writer.append(HEADER).append("\n");
            // Все задачи
            for (Task t : getTasks().values()) {
                writer.append(toCsvString(t)).append("\n");
            }
            // Все эпики
            for (Epic e : getEpics().values()) {
                writer.append(toCsvString(e)).append("\n");
            }
            // Все подзадачи
            for (Subtask s : getSubtasks().values()) {
                writer.append(toCsvString(s)).append("\n");
            }
            // Разделитель
            writer.append("\n");
            // История просмотров
            writer.append(historyToString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл", e);
        }
    }

    private String historyToString() {
        return getHistory().stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        if (!file.exists()) return manager;

        manager.isLoading = true;
        try {
            // очистка
            manager.getTasks().clear();
            manager.getEpics().clear();
            manager.getSubtasks().clear();
            manager.getHistoryManager().getHistory().clear();

            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) return manager;

            // читаем до пустой строки
            int i = 1;
            for (; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isEmpty()) break;
                Task task = fromString(line);
                switch (task.getClass().getSimpleName().toUpperCase()) {
                    case "TASK":    manager.addTask(task);    break;
                    case "EPIC":    manager.addEpic((Epic) task);    break;
                    case "SUBTASK": manager.addSubtask((Subtask) task); break;
                }
            }

            // восстановление истории
            if (i + 1 < lines.size()) {
                String hist = lines.get(i + 1);
                if (!hist.isEmpty()) {
                    for (String idStr : hist.split(",")) {
                        int id = Integer.parseInt(idStr);
                        if (manager.getTasks().containsKey(id)) {
                            manager.getHistoryManager().add(manager.getTasks().get(id));
                        } else if (manager.getEpics().containsKey(id)) {
                            manager.getHistoryManager().add(manager.getEpics().get(id));
                        } else if (manager.getSubtasks().containsKey(id)) {
                            manager.getHistoryManager().add(manager.getSubtasks().get(id));
                        }
                    }
                }
            }

            // пересчитать время эпиков
            for (Epic epic : manager.getEpics().values()) {
                epic.updateTimeMetrics(manager.getSubtasks());
            }

            // вычислить следующий ID
            int maxId = 0;
            for (Task t : manager.getTasks().values())    maxId = Math.max(maxId, t.getId());
            for (Epic e : manager.getEpics().values())    maxId = Math.max(maxId, e.getId());
            for (Subtask s : manager.getSubtasks().values()) maxId = Math.max(maxId, s.getId());
            manager.setNextId(maxId + 1);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла", e);
        } finally {
            manager.isLoading = false;
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String desc = parts[4];
        String epicPart = parts[5];

        // parse duration & startTime
        long minutes = parts.length > 6 && !parts[6].isEmpty()
                ? Long.parseLong(parts[6])
                : 0L;
        Duration duration = Duration.ofMinutes(minutes);
        LocalDateTime start = parts.length > 7 && !parts[7].isEmpty()
                ? LocalDateTime.parse(parts[7])
                : null;

        Task task;
        switch (type) {
            case "TASK":
                task = new Task(id, name, desc, status);
                break;
            case "EPIC":
                task = new Epic(id, name, desc, status, new ArrayList<>());
                break;
            case "SUBTASK":
                int epicId = epicPart.isEmpty() ? 0 : Integer.parseInt(epicPart);
                task = new Subtask(id, name, desc, status, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setDuration(duration);
        task.setStartTime(start);
        return task;
    }

    // доступ к защищённому API родителя
    @Override protected Map<Integer, Task> getTasks()      { return super.getTasks(); }
    @Override protected Map<Integer, Epic> getEpics()      { return super.getEpics(); }
    @Override protected Map<Integer, Subtask> getSubtasks(){ return super.getSubtasks(); }
    @Override protected HistoryManager getHistoryManager() { return super.getHistoryManager(); }
    @Override protected void setNextId(int nextId)         { super.setNextId(nextId); }
}
