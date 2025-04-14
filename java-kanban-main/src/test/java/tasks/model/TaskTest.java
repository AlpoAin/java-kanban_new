package tasks.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTaskCreation() {
        Task task = new Task("Задача1", "Описание задачи");
        assertEquals("Задача1", task.getName());
        assertEquals("Описание задачи", task.getDescription());
        assertEquals(0, task.getId());
        assertEquals(Status.NEW, task.getStatus());
    }
}
