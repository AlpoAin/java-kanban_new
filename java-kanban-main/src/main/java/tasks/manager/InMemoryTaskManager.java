package tasks.manager;

import tasks.model.Task;
import tasks.model.Epic;
import tasks.model.Subtask;
import java.util.*;
import java.time.LocalDateTime;

/**
 * Реализация TaskManager, хранящая всё в памяти,
 * поддерживает приоритизацию по времени и проверку пересечений.
 */
public class InMemoryTaskManager implements TaskManager {
    private int nextId = 1;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    // Все задачи и подзадачи с ненулевым startTime, отсортированные по startTime
    private final NavigableSet<Task> prioritized = new TreeSet<>(
            Comparator.comparing(
                    Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())
            )
    );

    @Override
    public void addTask(Task task) {
        if (task == null) return;
        // Проверяем пересечение
        if (task.getStartTime() != null &&
                prioritized.stream().anyMatch(o -> TaskUtils.isOverlapping(o, task))) {
            throw new IllegalArgumentException("Задача пересекается по времени");
        }
        // Присвоение ID
        if (task.getId() == 0) {
            task.setId(nextId++);
        } else if (task.getId() >= nextId) {
            nextId = task.getId() + 1;
        }
        tasks.put(task.getId(), task);
        // Добавляем в приоритеты
        if (task.getStartTime() != null) {
            prioritized.add(task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) return;
        if (epic.getId() == 0) {
            epic.setId(nextId++);
        } else if (epic.getId() >= nextId) {
            nextId = epic.getId() + 1;
        }
        epics.put(epic.getId(), epic);
        // Эпики без времени не попадают в prioritized
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (subtask == null) return;
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Epic не найден");
        }
        // Проверка пересечения
        if (subtask.getStartTime() != null &&
                prioritized.stream().anyMatch(o -> TaskUtils.isOverlapping(o, subtask))) {
            throw new IllegalArgumentException("Подзадача пересекается по времени");
        }
        // ID
        if (subtask.getId() == 0) {
            subtask.setId(nextId++);
        } else if (subtask.getId() >= nextId) {
            nextId = subtask.getId() + 1;
        }
        subtasks.put(subtask.getId(), subtask);
        // Привязка к эпику
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        // Пересчёт времени эпика
        epic.updateTimeMetrics(subtasks);
        // Добавляем в приоритеты
        if (subtask.getStartTime() != null) {
            prioritized.add(subtask);
        }
    }

    @Override
    public Task getTask(int id) {
        Task t = tasks.get(id);
        if (t != null) historyManager.add(t);
        return t;
    }

    @Override
    public Epic getEpic(int id) {
        Epic e = epics.get(id);
        if (e != null) historyManager.add(e);
        return e;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask s = subtasks.get(id);
        if (s != null) historyManager.add(s);
        return s;
    }

    @Override
    public void removeTask(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            historyManager.remove(id);
            prioritized.remove(removed);
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic removed = epics.remove(id);
        if (removed != null) {
            // Удаляем сабтаски
            for (Integer sid : removed.getSubtaskIds()) {
                subtasks.remove(sid);
                historyManager.remove(sid);
                prioritized.removeIf(t -> t.getId() == sid);
            }
            historyManager.remove(id);
            prioritized.remove(removed);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            // 1) убрать ID из эпика
            Epic epic = epics.get(removed.getEpicId());
            if (epic != null) {
                List<Integer> list = new ArrayList<>(epic.getSubtaskIds());
                list.remove(Integer.valueOf(id));
                epic.setSubtaskIds(list);
                // 2) пересчитать время
                epic.updateTimeMetrics(subtasks);
            }
            historyManager.remove(id);
            // 3) убрать из приоритетов
            prioritized.remove(removed);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**
     * Возвращает задачи и подзадачи, отсортированные по времени старта.
     */
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritized);
    }

    // Для FileBackedTaskManager — доступ к защищённым данным родителя
    protected Map<Integer, Task> getTasks() {
        return tasks;
    }

    protected Map<Integer, Epic> getEpics() {
        return epics;
    }

    protected Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected void setNextId(int nextId) {
        this.nextId = nextId;
    }

}
