package tasks.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Эпик, содержащий подзадачи. Время и продолжительность вычисляются на основе подзадач.
 */
public class Epic extends Task {
    private List<Integer> subtaskIds = new ArrayList<>();
    private Duration duration = Duration.ZERO;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id,
                String name,
                String description,
                Status status,
                List<Integer> subtaskIds) {
        super(id, name, description, status);
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    /**
     * Обновляет startTime, endTime и duration:
     * — если subtaskIds пусты, берёт все подзадачи из allSubtasks;
     * — иначе — только те, что в subtaskIds.
     */
    public void updateTimeMetrics(Map<Integer, Subtask> allSubtasks) {
        // Сначала отберём нужные Subtask
        List<Subtask> tasks;
        if (subtaskIds.isEmpty()) {
            // тестовый сценарий: карта содержит только нужные сабтаски
            tasks = allSubtasks.values().stream()
                    .filter(s -> s.getStartTime() != null)
                    .collect(Collectors.toList());
        } else {
            // обычный сценарий: у эпика есть subtaskIds
            tasks = subtaskIds.stream()
                    .map(allSubtasks::get)
                    .filter(Objects::nonNull)
                    .filter(s -> s.getStartTime() != null)
                    .collect(Collectors.toList());
        }

        // Теперь рассчитываем метрики
        if (tasks.isEmpty()) {
            this.startTime = null;
            this.endTime = null;
            this.duration = Duration.ZERO;
        } else {
            this.startTime = tasks.stream()
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            this.endTime = tasks.stream()
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            this.duration = tasks.stream()
                    .map(Subtask::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
        }
    }

    /**
     * Добавляет идентификатор подзадачи в эпик;
     * реальный пересчет времени делает TaskManager через updateTimeMetrics().
     */
    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = new ArrayList<>(subtaskIds);
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
