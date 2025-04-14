package tasks.manager;

import tasks.model.Epic;
import tasks.model.Status;
import tasks.model.Subtask;
import tasks.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private List<Task> history = new LinkedList<>();

    @Override
    public void addTask(Task task) {
        if (task == null) return;
        if (task.getId() == 0) {
            task.setId(nextId++);
        } else {
            if (task.getId() >= nextId) {
                nextId = task.getId() + 1;
            }
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) return;
        if (epic.getId() == 0) {
            epic.setId(nextId++);
        } else {
            if (epic.getId() >= nextId) {
                nextId = epic.getId() + 1;
            }
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) return;
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic с id " + subtask.getEpicId() + " не найден");
        }
        if (subtask.getId() == 0) {
            subtask.setId(nextId++);
        } else {
            if (subtask.getId() >= nextId) {
                nextId = subtask.getId() + 1;
            }
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            recordHistory(task);
            return new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
        }
        return null;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            recordHistory(epic);
            return new Epic(epic.getId(), epic.getName(), epic.getDescription(), epic.getStatus(), epic.getSubtaskIds());
        }
        return null;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            recordHistory(subtask);
            return new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicId());
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyView = new ArrayList<>();
        for (Task item : history) {
            if (item instanceof Epic) {
                Epic e = (Epic) item;
                historyView.add(new Epic(e.getId(), e.getName(), e.getDescription(), e.getStatus(), e.getSubtaskIds()));
            } else if (item instanceof Subtask) {
                Subtask s = (Subtask) item;
                historyView.add(new Subtask(s.getId(), s.getName(), s.getDescription(), s.getStatus(), s.getEpicId()));
            } else if (item != null) {
                historyView.add(new Task(item.getId(), item.getName(), item.getDescription(), item.getStatus()));
            }
        }
        return historyView;
    }

    private void recordHistory(Task task) {
        history.add(task);
        if (history.size() > 10) {
            ((LinkedList<Task>) history).removeFirst();
        }
    }
}
