//package tasks.app;
//
//import tasks.manager.Managers;
//import tasks.manager.TaskManager;
//import tasks.model.Epic;
//import tasks.model.Status;
//import tasks.model.Subtask;
//import tasks.model.Task;
//import tasks.manager.FileBackedTaskManager;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//public class Main {
//    public static void main(String[] args) {
//        Path path = Paths.get("tasks.csv");
//        TaskManager manager = FileBackedTaskManager.loadFromFile(path.toFile());
//
//
//        Task task1 = new Task("Задача 1", "Описание задачи 1");
//        manager.addTask(task1);
//
//        Task task2 = new Task("Задача 2", "Описание задачи 2");
//        manager.addTask(task2);
//
//        Epic epic1 = new Epic("Эпик 1", "Эпик с двумя подзадачами");
//        manager.addEpic(epic1);
//
//        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
//        manager.addSubtask(subtask1);
//
//        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
//        manager.addSubtask(subtask2);
//
//        Epic epic2 = new Epic("Эпик 2", "Эпик с одной подзадачей");
//        manager.addEpic(epic2);
//
//        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic2.getId());
//        manager.addSubtask(subtask3);
//
//        System.out.println("=== Начальный список задач ===");
//        System.out.println("Задача1: " + manager.getTask(task1.getId()));
//        System.out.println("Задача2: " + manager.getTask(task2.getId()));
//        System.out.println("Эпик1: " + manager.getEpic(epic1.getId()));
//        System.out.println("Эпик2: " + manager.getEpic(epic2.getId()));
//        System.out.println("Подзадача1: " + manager.getSubtask(subtask1.getId()));
//        System.out.println("Подзадача2: " + manager.getSubtask(subtask2.getId()));
//        System.out.println("Подзадача3: " + manager.getSubtask(subtask3.getId()));
//
//        task1.setStatus(Status.DONE);
//        Subtask st1 = manager.getSubtask(subtask1.getId());
//        st1.setStatus(Status.DONE);
//        Subtask st3 = manager.getSubtask(subtask3.getId());
//        st3.setStatus(Status.IN_PROGRESS);
//
//        System.out.println("\n=== После изменения статусов ===");
//        System.out.println("Задача1: " + manager.getTask(task1.getId()));
//        System.out.println("Задача2: " + manager.getTask(task2.getId()));
//        System.out.println("Эпик1: " + manager.getEpic(epic1.getId()));
//        System.out.println("Эпик2: " + manager.getEpic(epic2.getId()));
//        System.out.println("Подзадача1: " + manager.getSubtask(subtask1.getId()));
//        System.out.println("Подзадача2: " + manager.getSubtask(subtask2.getId()));
//        System.out.println("Подзадача3: " + manager.getSubtask(subtask3.getId()));
//
//        System.out.println("\n=== История просмотров ===");
//        System.out.println(manager.getHistory());
//    }
//}

package tasks.app;

import tasks.manager.FileBackedTaskManager;
import tasks.manager.TaskManager;
import tasks.model.Task;
import tasks.model.Epic;
import tasks.model.Subtask;
import tasks.model.Status;

import java.io.File;

public class Main {
    private static final String FILENAME = "tasks.csv";

    public static void main(String[] args) {
        File file = new File(FILENAME);
        // Удаляем старый файл, чтобы каждый запуск был чистым
        if (file.exists()) {
            file.delete();
        }

        // Создаём/загружаем менеджер
        TaskManager manager = FileBackedTaskManager.loadFromFile(file);

        // Добавляем задачи, эпики и подзадачи
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        manager.addTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.addTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Эпик с двумя подзадачами");
        manager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        manager.addSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Эпик с одной подзадачей");
        manager.addEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic2.getId());
        manager.addSubtask(subtask3);

        // Выводим текущее состояние
        System.out.println("=== Начальный список задач ===");
        System.out.println("Задача1:    " + manager.getTask(task1.getId()));
        System.out.println("Задача2:    " + manager.getTask(task2.getId()));
        System.out.println("Эпик1:      " + manager.getEpic(epic1.getId()));
        System.out.println("Эпик2:      " + manager.getEpic(epic2.getId()));
        System.out.println("Подзадача1: " + manager.getSubtask(subtask1.getId()));
        System.out.println("Подзадача2: " + manager.getSubtask(subtask2.getId()));
        System.out.println("Подзадача3: " + manager.getSubtask(subtask3.getId()));

        // Меняем статусы (они сохранятся автоматически)
        manager.getTask(task1.getId()).setStatus(Status.DONE);
        manager.getSubtask(subtask1.getId()).setStatus(Status.DONE);
        manager.getSubtask(subtask3.getId()).setStatus(Status.IN_PROGRESS);

        System.out.println("\n=== После изменения статусов ===");
        System.out.println("Задача1:    " + manager.getTask(task1.getId()));
        System.out.println("Задача2:    " + manager.getTask(task2.getId()));
        System.out.println("Эпик1:      " + manager.getEpic(epic1.getId()));
        System.out.println("Эпик2:      " + manager.getEpic(epic2.getId()));
        System.out.println("Подзадача1: " + manager.getSubtask(subtask1.getId()));
        System.out.println("Подзадача2: " + manager.getSubtask(subtask2.getId()));
        System.out.println("Подзадача3: " + manager.getSubtask(subtask3.getId()));

        // И, наконец, история просмотров
        System.out.println("\n=== История просмотров ===");
        System.out.println(manager.getHistory());
    }
}



