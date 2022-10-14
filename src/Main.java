import model.*;
import service.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = Managers.getDefault();

        Task firstTask = new Task("Task 1", "Some moves", TaskStatus.NEW);
        Task secondTask = new Task("Task 2", "Some extra moves", TaskStatus.NEW);
        Epic firstEpic = new Epic("Новая задача 1", "Эпик с 2 подзадачами", TaskStatus.NEW);
        Epic secondEpic = new Epic("Новая задача 2", "Эпик с 1 подзадачей", TaskStatus.NEW);
        Subtask firstSubtask = new Subtask("Подзадача 1", "выполнить половину 1 эпика 1", TaskStatus.NEW);
        Subtask secondSubtask = new Subtask("Подзадача 2", "выполнить половину 2 эпика 1", TaskStatus.NEW);
        Subtask thirdSubtask = new Subtask("Подзадача 3", "выполнить эпик 2", TaskStatus.NEW);

        manager.add(firstTask);
        manager.add(secondTask);
        manager.add(firstEpic);
        manager.add(secondEpic);
        firstSubtask.setEpicId(firstEpic.getId());
        secondSubtask.setEpicId(firstEpic.getId());
        manager.add(firstSubtask);
        manager.add(secondSubtask);
        thirdSubtask.setEpicId(secondEpic.getId());
        manager.add(thirdSubtask);

        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getEpicSubtasks(firstEpic.getId()));
        System.out.println(manager.getEpic(firstEpic.getId()));
        System.out.println("=".repeat(75));

        Subtask updFirstSubtask = new Subtask("Обновленная первая подзадача",
                "Сделать первую часть эпика 1 по-другому", TaskStatus.IN_PROGRESS);
        Subtask updThirdSubtask = new Subtask("Обновленная третья подзачада",
                "Подзадача выполнена", TaskStatus.DONE);
        updFirstSubtask.setIdentityNumber(firstSubtask.getId());
        updThirdSubtask.setIdentityNumber(thirdSubtask.getId());
        manager.updateSubtaskById(updFirstSubtask);
        manager.updateSubtaskById(updThirdSubtask);

        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getEpicSubtasks(firstEpic.getId()));
        System.out.println(manager.getEpicSubtasks(secondEpic.getId()));
        System.out.println(manager.getEpic(secondEpic.getId()));
        System.out.println("=".repeat(75));

        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(secondTask.getId()));
        System.out.println(manager.getSubtask(firstSubtask.getId()));
        System.out.println(manager.getSubtask(thirdSubtask.getId()));
        System.out.println("=".repeat(75));

        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("=".repeat(75));

        manager.removeTaskById(secondEpic.getId());
        manager.removeTaskById(updThirdSubtask.getId());

        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println("=".repeat(75));

        manager.removeAllRegularTasks();
        manager.removeAllEpics();
        manager.removeAllSubTasks();

        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
    }
}