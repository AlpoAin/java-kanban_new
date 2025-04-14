package tasks.manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Task;
import tasks.model.Status;
import java.util.List;

class InMemoryTaskManagerTest {

    @Test
    void testAddTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("TaskA", "DescA");
        manager.addTask(task);
        assertEquals(task, manager.getTask(task.getId()));
    }

    @Test
    void testAddEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("EpicA", "DescEpic");
        manager.addEpic(epic);
        assertEquals(epic, manager.getEpic(epic.getId()));
    }

    @Test
    void testAddSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("EpicForSubtask", "DescE");
        manager.addEpic(epic);
        Subtask sub = new Subtask("Sub1", "DescS", epic.getId());
        manager.addSubtask(sub);
        assertEquals(sub, manager.getSubtask(sub.getId()));
        assertTrue(manager.getEpic(epic.getId()).getSubtaskIds().contains(sub.getId()));
    }

    @Test
    void testHistory() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task t1 = new Task("T1", "D1");
        Task t2 = new Task("T2", "D2");
        manager.addTask(t1);
        manager.addTask(t2);
        manager.getTask(t1.getId());
        manager.getTask(t2.getId());
        List<Task> history = manager.getHistory();
        assertEquals(2, history.size());
        assertEquals(t2, history.get(1));
    }
}
