package tasks.manager;

import org.junit.jupiter.api.Test;
import tasks.model.Status;
import tasks.model.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testEmptyHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testAddSingleTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Desc1", Status.NEW);
        historyManager.add(task1);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
    }

    @Test
    void testAddDuplicateTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Desc1", Status.NEW);
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testAddMultipleTasksOrder() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1", Status.NEW);
        Task t2 = new Task(2, "Task2", "Desc2", Status.NEW);
        Task t3 = new Task(3, "Task3", "Desc3", Status.NEW);
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(t1, history.get(0));
        assertEquals(t2, history.get(1));
        assertEquals(t3, history.get(2));
    }

    @Test
    void testRemoveFromHistoryHead() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1", Status.NEW);
        Task t2 = new Task(2, "Task2", "Desc2", Status.NEW);
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.remove(t1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(t2, history.get(0));
    }

    @Test
    void testRemoveFromHistoryTail() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1", Status.NEW);
        Task t2 = new Task(2, "Task2", "Desc2", Status.NEW);
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.remove(t2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(t1, history.get(0));
    }

    @Test
    void testRemoveFromHistoryMiddle() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1", Status.NEW);
        Task t2 = new Task(2, "Task2", "Desc2", Status.NEW);
        Task t3 = new Task(3, "Task3", "Desc3", Status.NEW);
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);
        historyManager.remove(t2.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(t1, history.get(0));
        assertEquals(t3, history.get(1));
    }

    @Test
    void testRemoveNonExistentTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task t1 = new Task(1, "Task1", "Desc1", Status.NEW);
        historyManager.add(t1);
        historyManager.remove(999);
        assertEquals(1, historyManager.getHistory().size());
    }
}
