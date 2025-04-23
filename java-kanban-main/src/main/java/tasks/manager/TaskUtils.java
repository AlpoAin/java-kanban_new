package tasks.manager;

import tasks.model.Task;
import java.time.LocalDateTime;

/**
 * Утилита для работы с задачами по времени.
 */
public class TaskUtils {
    /**
     * Проверяет наложение двух задач по времени.
     * @return true, если отрезки [start, end] пересекаются.
     */
    public static boolean isOverlapping(Task a, Task b) {
        if (a.getStartTime() == null || b.getStartTime() == null) {
            return false;
        }
        LocalDateTime aStart = a.getStartTime();
        LocalDateTime aEnd   = a.getEndTime();
        LocalDateTime bStart = b.getStartTime();
        LocalDateTime bEnd   = b.getEndTime();

        // пересекаются, если ни один отрезок не "заканчивается" раньше начала другого
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }
}
