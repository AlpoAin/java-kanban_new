package tasks.manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.model.Epic;
import tasks.model.Status;
import tasks.model.Subtask;
import tasks.model.Task;

class TaskManagerTest {

    @Test
    void testAddAndGetTask() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Task1", "Desc1");
        manager.addTask(task);
        Task fetched = manager.getTask(task.getId());
        assertEquals(task, fetched);
    }

    @Test
    void testAddAndGetEpic() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("Epic1", "EpicDesc");
        manager.addEpic(epic);
        Epic fetched = manager.getEpic(epic.getId());
        assertEquals(epic, fetched);
    }

    @Test
    void testAddAndGetSubtask() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("EpicForSub", "DescEpic");
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Sub1", "DescSub", epic.getId());
        manager.addSubtask(subtask);
        Subtask fetched = manager.getSubtask(subtask.getId());
        assertEquals(subtask, fetched);
        assertTrue(manager.getEpic(epic.getId()).getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    void testHistory() {
        TaskManager manager = Managers.getDefault();
        Task t1 = new Task("T1", "D1");
        Task t2 = new Task("T2", "D2");
        manager.addTask(t1);
        manager.addTask(t2);
        manager.getTask(t1.getId());
        manager.getTask(t2.getId());
        assertEquals(2, manager.getHistory().size());
        assertEquals(t2, manager.getHistory().get(1));
    }
}
