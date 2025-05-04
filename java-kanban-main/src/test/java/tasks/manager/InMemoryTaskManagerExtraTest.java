package tasks.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.model.Task;
import tasks.model.Subtask;
import tasks.model.Epic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerExtraTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void getPrioritizedTasksOrdersByStartTime() {
        Task t1 = new Task("T1","");
        t1.setStartTime(LocalDateTime.of(2025,1,1,12,0));
        t1.setDuration(Duration.ofMinutes(30));

        Task t2 = new Task("T2","");
        t2.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        t2.setDuration(Duration.ofMinutes(60));

        manager.addTask(t1);
        manager.addTask(t2);

        List<Task> pr = manager.getPrioritizedTasks();
        assertEquals(List.of(t2, t1), pr);
    }

    @Test
    void tasksWithoutStartTimeIgnoredInPrioritized() {
        Task t1 = new Task("T1",""); // no time
        Task t2 = new Task("T2","");
        t2.setStartTime(LocalDateTime.of(2025,1,1,11,0));
        t2.setDuration(Duration.ofMinutes(15));

        manager.addTask(t1);
        manager.addTask(t2);

        List<Task> pr = manager.getPrioritizedTasks();
        assertEquals(List.of(t2), pr);
    }

    @Test
    void addTaskThrowsOnOverlap() {
        Task a = new Task("A","");
        a.setStartTime(LocalDateTime.of(2025,1,2,9,0));
        a.setDuration(Duration.ofMinutes(60));

        Task b = new Task("B","");
        b.setStartTime(LocalDateTime.of(2025,1,2,9,30));
        b.setDuration(Duration.ofMinutes(30));

        manager.addTask(a);
        assertThrows(IllegalArgumentException.class, () -> manager.addTask(b));
    }

    @Test
    void addSubtaskThrowsOnOverlap() {
        Epic epic = new Epic("E",""); manager.addEpic(epic);
        Subtask s1 = new Subtask("S1","", epic.getId());
        s1.setStartTime(LocalDateTime.of(2025,1,3,8,0));
        s1.setDuration(Duration.ofMinutes(45));
        manager.addSubtask(s1);

        Subtask s2 = new Subtask("S2","", epic.getId());
        s2.setStartTime(LocalDateTime.of(2025,1,3,8,30));
        s2.setDuration(Duration.ofMinutes(30));

        assertThrows(IllegalArgumentException.class, () -> manager.addSubtask(s2));
    }
}
