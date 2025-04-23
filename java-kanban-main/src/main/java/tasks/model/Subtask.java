package tasks.model;
import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        setDuration(null);
        setStartTime(null);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        setDuration(null);
        setStartTime(null);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
