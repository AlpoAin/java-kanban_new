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

    @Test
    void testRemoveTask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task t1 = new Task("T1", "D1");
        manager.addTask(t1);
        manager.getTask(t1.getId());
        manager.removeTask(t1.getId());
        assertNull(manager.getTask(t1.getId()));
        assertFalse(manager.getHistory().contains(t1));
    }

    @Test
    void testRemoveEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "EpicDesc");
        manager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        Subtask sub2 = new Subtask("Sub2", "Desc", epic.getId());
        manager.addSubtask(sub1);
        manager.addSubtask(sub2);
        manager.getEpic(epic.getId());
        manager.getSubtask(sub1.getId());
        manager.removeEpic(epic.getId());
        assertNull(manager.getEpic(epic.getId()));
        assertNull(manager.getSubtask(sub1.getId()));
        assertNull(manager.getSubtask(sub2.getId()));
        assertFalse(manager.getHistory().contains(epic));
        assertFalse(manager.getHistory().contains(sub1));
        assertFalse(manager.getHistory().contains(sub2));
    }

    @Test
    void testRemoveSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic1", "EpicDesc");
        manager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        manager.addSubtask(sub1);
        manager.getSubtask(sub1.getId());
        manager.removeSubtask(sub1.getId());
        assertNull(manager.getSubtask(sub1.getId()));
        assertFalse(manager.getHistory().contains(sub1));
        assertFalse(manager.getEpic(epic.getId()).getSubtaskIds().contains(sub1.getId()));
    }

    @Test
    void testUpdateTaskStatus() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Task task = new Task("UpdTask", "Desc");
        manager.addTask(task);
        task.setStatus(Status.DONE);
        Task fetched = manager.getTask(task.getId());
        assertEquals(Status.DONE, fetched.getStatus());
    }

    @Test
    void testUpdateSubtaskStatus() {
        InMemoryTaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("EpicTest", "Desc");
        manager.addEpic(epic);
        Subtask sub1 = new Subtask("Sub1", "Desc", epic.getId());
        manager.addSubtask(sub1);
        sub1.setStatus(Status.DONE);
        assertEquals(Status.DONE, manager.getSubtask(sub1.getId()).getStatus());
    }
}
