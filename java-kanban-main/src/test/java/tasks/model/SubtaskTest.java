package tasks.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testSubtaskCreation() {
        Subtask subtask = new Subtask("Подзадача1", "Описание подзадачи", 10);
        assertEquals("Подзадача1", subtask.getName());
        assertEquals("Описание подзадачи", subtask.getDescription());
        assertEquals(10, subtask.getEpicId());
        assertEquals(0, subtask.getId());
        assertEquals(Status.NEW, subtask.getStatus());
    }
}
