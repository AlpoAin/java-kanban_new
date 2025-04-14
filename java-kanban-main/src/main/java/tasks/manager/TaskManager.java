package tasks.manager;

import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(Subtask subtask);

    Task getTask(int id);
    Epic getEpic(int id);
    Subtask getSubtask(int id);

    List<Task> getHistory();
}
