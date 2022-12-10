import model.*;
import service.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();
        manager.removeAllRegularTasks();

        Task firstTask = new Task("Задача 1", "Some moves", TaskStatus.NEW);
        Task secondTask = new Task("Задача 2", "Some extra moves", TaskStatus.NEW);
        Epic firstEpic = new Epic("Эпик 1", "Эпик с 3 подзадачами", TaskStatus.NEW);
        Epic secondEpic = new Epic("Эпик 2", "Эпик без подзадач", TaskStatus.NEW);
        Subtask firstSubtask = new Subtask("Подзадача 1", "выполнить половину 1 эпика 1", TaskStatus.NEW);
        Subtask secondSubtask = new Subtask("Подзадача 2", "выполнить половину 2 эпика 1", TaskStatus.NEW);
        Subtask thirdSubtask = new Subtask("Подзадача 3", "выполнить половину 3 эпика 1", TaskStatus.NEW);

        manager.add(firstTask);
        manager.add(secondTask);
        manager.add(firstEpic);
        manager.add(secondEpic);
        firstSubtask.setEpicId(firstEpic.getId());
        secondSubtask.setEpicId(firstEpic.getId());
        manager.add(firstSubtask);
        manager.add(secondSubtask);
        thirdSubtask.setEpicId(firstEpic.getId());
        manager.add(thirdSubtask);

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

        System.out.println("Ещё немного вызовов задач(не истории):");
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(secondTask.getId()));
        System.out.println(manager.getSubtask(firstSubtask.getId()));
        System.out.println(manager.getSubtask(thirdSubtask.getId()));
        System.out.println("=".repeat(75));

        System.out.println("Новая история вызовов задач:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("=".repeat(75));

        System.out.println("Удаление задач: Эпик2, Сабтаск 2, Эпик 1, Задача 2");
        manager.removeTaskById(secondEpic.getId());
        manager.removeTaskById(secondSubtask.getId());
        manager.removeTaskById(firstEpic.getId());
        manager.removeTaskById(secondTask.getId());

        System.out.println("Имеющиеся задачи:");
        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println("=".repeat(75));

        System.out.println("Обновленная история задач после их удаления:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("=".repeat(75));

        manager.removeAllRegularTasks();
        manager.removeAllEpics();
        manager.removeAllSubtasks();

        System.out.println("Печать списков задач и истории вызовов после удаления всех задач:");
        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("=".repeat(75));
    }
}