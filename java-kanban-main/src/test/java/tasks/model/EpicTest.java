package tasks.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEpicCreation() {
        Epic epic = new Epic("Эпик1", "Описание эпика");
        assertEquals("Эпик1", epic.getName());
        assertEquals("Описание эпика", epic.getDescription());
        assertEquals(0, epic.getId());
        assertTrue(epic.getSubtaskIds().isEmpty());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void testAddSubtask() {
        Epic epic = new Epic("Эпик2", "Описание эпика2");
        epic.addSubtask(101);
        assertFalse(epic.getSubtaskIds().isEmpty());
        assertTrue(epic.getSubtaskIds().contains(101));
    }
}
