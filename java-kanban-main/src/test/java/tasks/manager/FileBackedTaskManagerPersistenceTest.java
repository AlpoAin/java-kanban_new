package tasks.manager;

import org.junit.jupiter.api.*;
import tasks.model.Task;
import tasks.model.Subtask;
import tasks.model.Epic;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerPersistenceTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void saveLoadTaskWithTimeAndDuration() {
        Task t = new Task("Task","Desc");
        t.setStartTime(LocalDateTime.of(2025,5,1,14,0));
        t.setDuration(Duration.ofMinutes(90));
        manager.addTask(t);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task t2 = loaded.getTask(t.getId());
        assertEquals(t.getStartTime(),  t2.getStartTime());
        assertEquals(t.getDuration(),   t2.getDuration());
    }

    @Test
    void saveLoadSubtaskAndEpicMetrics() {
        Epic epic = new Epic("Epic","Desc");
        manager.addEpic(epic);

        Subtask s = new Subtask("Sub","Desc", epic.getId());
        s.setStartTime(LocalDateTime.of(2025,5,2,9,30));
        s.setDuration(Duration.ofMinutes(45));
        manager.addSubtask(s);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Epic e2 = loaded.getEpic(epic.getId());
        assertEquals(Duration.ofMinutes(45),            e2.getDuration());
        assertEquals(LocalDateTime.of(2025,5,2,9,30),    e2.getStartTime());
        assertEquals(LocalDateTime.of(2025,5,2,10,15),   e2.getEndTime());
    }
}
