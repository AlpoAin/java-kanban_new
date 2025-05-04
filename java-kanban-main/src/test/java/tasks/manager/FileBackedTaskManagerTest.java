package tasks.manager;

import org.junit.jupiter.api.*;
import tasks.model.Task;
import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Status;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        manager = null;
    }

    @Test
    void testSaveLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
        assertTrue(loaded.getSubtasks().isEmpty());
        assertTrue(loaded.getHistory().isEmpty());
    }

    @Test
    void testSaveLoadWithData() {
        Task t1 = new Task("Task1", "Desc1");
        manager.addTask(t1);

        Epic e1 = new Epic("Epic1", "DescE1");
        manager.addEpic(e1);

        Subtask s1 = new Subtask("Sub1", "DescS1", e1.getId());
        manager.addSubtask(s1);

        // формируем историю
        manager.getTask(t1.getId());
        manager.getEpic(e1.getId());
        manager.getSubtask(s1.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loaded.getTasks().size());
        assertEquals(t1, loaded.getTask(t1.getId()));

        assertEquals(1, loaded.getEpics().size());
        assertEquals(e1, loaded.getEpic(e1.getId()));

        assertEquals(1, loaded.getSubtasks().size());
        assertEquals(s1, loaded.getSubtask(s1.getId()));

        List<Task> history = loaded.getHistory();
        assertEquals(3, history.size());
        assertEquals(t1.getId(), history.get(0).getId());
        assertEquals(e1.getId(), history.get(1).getId());
        assertEquals(s1.getId(), history.get(2).getId());
    }
}
