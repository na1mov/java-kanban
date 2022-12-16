import model.*;
import service.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Поехали!");
        KVServer kvServer = new KVServer();
        kvServer.start();

        TaskManager manager = new HttpTasksManager("http://localhost:8080");

        Task firstTask = new Task("Задача 1", "Some moves", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Task secondTask = new Task("Задача 2", "Some extra moves", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        Epic firstEpic = new Epic("Эпик 1", "Эпик с 3 подзадачами", TaskStatus.NEW);
        Epic secondEpic = new Epic("Эпик 2", "Эпик без подзадач", TaskStatus.NEW);

        manager.add(firstTask);
        manager.add(secondTask);
        manager.add(firstEpic);
        manager.add(secondEpic);

        Subtask firstSubtask = new Subtask("Подзадача 1", "выполнить половину 1 эпика 1",
                TaskStatus.NEW, firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 13, 30));
        Subtask secondSubtask = new Subtask("Подзадача 2", "выполнить половину 2 эпика 1",
                TaskStatus.NEW, firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 14, 30));
        Subtask thirdSubtask = new Subtask("Подзадача 3", "выполнить половину 3 эпика 1",
                TaskStatus.NEW, firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 15, 30));

        manager.add(firstSubtask);
        manager.add(secondSubtask);
        manager.add(thirdSubtask);

        Task thirdTask = new Task("Task 3", "Task 3 with null startTime", TaskStatus.NEW);
        manager.add(thirdTask);
        System.out.println("=".repeat(75));
        System.out.println("Печать списка задач с приоритетом по времени");
        System.out.println(manager.getPrioritizedTasks());
        System.out.println("=".repeat(75));

        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getEpicSubtasks(firstEpic.getId()));
        System.out.println("=".repeat(75));

        System.out.println("Печать вызовов задач(не история, возможны повторы):");
        System.out.println(manager.getEpic(firstEpic.getId()));
        System.out.println(manager.getTask(secondTask.getId()));
        System.out.println(manager.getEpic(secondEpic.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getSubtask(secondSubtask.getId()));
        System.out.println(manager.getSubtask(secondSubtask.getId()));
        System.out.println("=".repeat(75));

        System.out.println("Печать истории вызовов задач(повторов нет):");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("=".repeat(75));

        System.out.println("Печать списков задач и истории вызовов из памяти:");
        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println("=".repeat(75));
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }

        System.out.println("=".repeat(75));
        System.out.println("=".repeat(75));
        System.out.println("Восстанавливаем httpManager используя http-server");
        HttpTasksManager secondManager = HttpTasksManager.loadFromUrl("http://localhost:8080");
        System.out.println("=".repeat(75));
        System.out.println("Печать списков задач и истории вызовов при бекапе из файла:");
        System.out.println(secondManager.getAllRegularTasks());
        System.out.println(secondManager.getAllEpics());
        System.out.println(secondManager.getAllSubTasks());
        System.out.println("=".repeat(75));
        System.out.println("=".repeat(75));
        for (Task task : secondManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("=".repeat(75));
        System.out.println("Восстановленный ID счётчик:");
        System.out.println(secondManager.getIdentityNumber());
        System.out.println("=".repeat(75));

        kvServer.stop();
    }
}