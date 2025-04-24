package tasks.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EpicTimeMetricsTest {
    private Epic epic;
    private Map<Integer, Subtask> subs;

    @BeforeEach
    void setup() {
        epic = new Epic("Epic","Test epic");
        subs = new HashMap<>();
    }

    @Test
    void emptyEpicHasZeroDurationAndNullTimes() {
        epic.updateTimeMetrics(subs);
        assertNull(epic.getStartTime(), "startTime должен быть null");
        assertNull(epic.getEndTime(),   "endTime должен быть null");
        assertEquals(Duration.ZERO, epic.getDuration(), "duration должен быть ноль");
    }

    @Test
    void singleSubtaskSetsEpicMetrics() {
        Subtask s = new Subtask("S","", 1);
        s.setId(1);
        s.setStartTime(LocalDateTime.of(2025,1,1,9,0));
        s.setDuration(Duration.ofMinutes(30)); // 9:00–9:30
        subs.put(1, s);

        epic.updateTimeMetrics(subs);
        assertEquals(s.getStartTime(), epic.getStartTime());
        assertEquals(s.getEndTime(),   epic.getEndTime());
        assertEquals(s.getDuration(),  epic.getDuration());
    }

    @Test
    void multipleSubtasksAggregateCorrectly() {
        Subtask s1 = new Subtask("S1","", 1);
        s1.setId(1);
        s1.setStartTime(LocalDateTime.of(2025,1,1,8,0));
        s1.setDuration(Duration.ofMinutes(30)); // 8:00–8:30

        Subtask s2 = new Subtask("S2","", 1);
        s2.setId(2);
        s2.setStartTime(LocalDateTime.of(2025,1,1,10,0));
        s2.setDuration(Duration.ofMinutes(60)); // 10:00–11:00

        subs.put(1, s1);
        subs.put(2, s2);

        epic.updateTimeMetrics(subs);
        assertEquals(LocalDateTime.of(2025,1,1,8,0),  epic.getStartTime());
        assertEquals(LocalDateTime.of(2025,1,1,11,0), epic.getEndTime());
        assertEquals(Duration.ofMinutes(90),          epic.getDuration());
    }
}
