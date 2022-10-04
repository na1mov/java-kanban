public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager manager = new TaskManager();

        Task firstTask = new Task("Task 1", "Some moves", TaskStatus.NEW);
        Task secondTask = new Task("Task 2", "Some extra moves", TaskStatus.NEW);
        Epic firstEpic = new Epic("Новая задача 1", "Эпик с 2 подзадачами", TaskStatus.NEW);
        Epic secondEpic = new Epic("Новая задача 2", "Эпик с 1 подзадачей", TaskStatus.NEW);
        Subtask firstSubtask = new Subtask("Подзадача 1", "выполнить половину 1 эпика 1", TaskStatus.NEW);
        Subtask secondSubtask = new Subtask("Подзадача 2", "выполнить половину 2 эпика 1", TaskStatus.NEW);
        Subtask thirdSubtask = new Subtask("Подзадача 3", "выполнить эпик 2", TaskStatus.NEW);

        manager.addTask(firstTask);
        manager.addTask(secondTask);
        manager.addEpic(firstEpic);
        manager.addEpic(secondEpic);
        firstSubtask.setEpicId(firstEpic.getId());
        secondSubtask.setEpicId(firstEpic.getId());
        manager.addSubtask(firstSubtask);
        manager.addSubtask(secondSubtask);
        thirdSubtask.setEpicId(secondEpic.getId());
        manager.addSubtask(thirdSubtask);

        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getEpicSubtasks(firstEpic.getId()));
        System.out.println(manager.getEpicById(firstEpic.getId()));
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
        System.out.println(manager.getEpicById(secondEpic.getId()));
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