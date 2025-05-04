package tasks.manager;

import java.io.File;

public class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        File file = new File("tasks.csv");    // или путь на ваш выбор
        return FileBackedTaskManager.loadFromFile(file);
    }
}
