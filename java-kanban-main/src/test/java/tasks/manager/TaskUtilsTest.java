package tasks.manager;

import org.junit.jupiter.api.Test;
import tasks.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskUtilsTest {

    @Test
    void overlappingWhenIntervalsIntersect() {
        Task a = new Task("A","");
        a.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        a.setDuration(Duration.ofMinutes(60)); // 10:00–11:00

        Task b = new Task("B","");
        b.setStartTime(LocalDateTime.of(2025,1,1,10,30));
        b.setDuration(Duration.ofMinutes(60)); // 10:30–11:30

        assertTrue(TaskUtils.isOverlapping(a, b));
    }

    @Test
    void overlappingWhenTouchingAtEndpoint() {
        Task a = new Task("A","");
        a.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        a.setDuration(Duration.ofMinutes(60)); // 10:00–11:00

        Task b = new Task("B","");
        b.setStartTime(LocalDateTime.of(2025,1,1,11,0));
        b.setDuration(Duration.ofMinutes(30)); // 11:00–11:30

        // по логике isOverlapping точки касания считаются перекрытием
        assertTrue(TaskUtils.isOverlapping(a, b));
    }

    @Test
    void notOverlappingWhenSeparate() {
        Task a = new Task("A","");
        a.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        a.setDuration(Duration.ofMinutes(30)); // 10:00–10:30

        Task b = new Task("B","");
        b.setStartTime(LocalDateTime.of(2025,1,1,11,0));
        b.setDuration(Duration.ofMinutes(30)); // 11:00–11:30

        assertFalse(TaskUtils.isOverlapping(a, b));
    }

    @Test
    void nullStartTimeNeverOverlaps() {
        Task a = new Task("A","");
        Task b = new Task("B","");
        // обе без времени старта
        assertFalse(TaskUtils.isOverlapping(a, b));

        // одна задача с временем, другая без
        a.setStartTime(LocalDateTime.now());
        a.setDuration(Duration.ofMinutes(10));
        assertFalse(TaskUtils.isOverlapping(a, b));
    }
}
