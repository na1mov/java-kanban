package service;

import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File filePath;

    public FileBackedTasksManager(File filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args) {
        FileBackedTasksManager manager = new FileBackedTasksManager(
                new File("c:\\Users\\aleks\\dev\\java-kanban\\resources\\save"));

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

        FileBackedTasksManager secondManager = FileBackedTasksManager.loadFromFile(
                new File("c:\\Users\\aleks\\dev\\java-kanban\\resources\\save"));

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
        System.out.println("=".repeat(75));
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(filePath)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (int i = 1; i < identityNumber; i++) {
                if (regularTasks.containsKey(i)) {
                    fileWriter.write(toCsvString(regularTasks.get(i)));
                } else if (epicTasks.containsKey(i)) {
                    fileWriter.write(toCsvString(epicTasks.get(i)));
                } else if (subTasks.containsKey(i)) {
                    fileWriter.write(toCsvString(subTasks.get(i)));
                }
            }
            fileWriter.write(" \n");
            fileWriter.write(historyToString(historyManager));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private String toCsvString(Task task) {
        StringBuilder sb = new StringBuilder();
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            sb.append(epic.getId());
            sb.append(',');
            sb.append("EPIC");
            sb.append(',');
            sb.append(epic.getName());
            sb.append(',');
            sb.append(epic.getStatus());
            sb.append(',');
            sb.append(epic.getDetails());
            sb.append(',');
            sb.append('\n');
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getId());
            sb.append(',');
            sb.append("SUBTASK");
            sb.append(',');
            sb.append(subtask.getName());
            sb.append(',');
            sb.append(subtask.getStatus());
            sb.append(',');
            sb.append(subtask.getDetails());
            sb.append(',');
            sb.append(subtask.getEpicId());
            sb.append(',');
            sb.append('\n');
        } else {
            sb.append(task.getId());
            sb.append(',');
            sb.append("TASK");
            sb.append(',');
            sb.append(task.getName());
            sb.append(',');
            sb.append(task.getStatus());
            sb.append(',');
            sb.append(task.getDetails());
            sb.append(',');
            sb.append('\n');
        }
        return sb.toString();
    }

    private Task fromString(String value) {
        String[] lineData = value.split(",");
        TaskStatus status = TaskStatus.NEW;
        switch (lineData[3]) {
            case "NEW":
                break;
            case "IN_PROGRESS":
                status = TaskStatus.IN_PROGRESS;
                break;
            case "DONE":
                status = TaskStatus.DONE;
                break;
        }
        if (lineData[1].equals("TASK")) {
            Task task = new Task(lineData[2], lineData[4], status);
            task.setIdentityNumber(Integer.parseInt(lineData[0]));
            regularTasks.put(task.getId(), task);
            return task;
        } else if (lineData[1].equals("EPIC")) {
            Epic epic = new Epic(lineData[2], lineData[4], status);
            epic.setIdentityNumber(Integer.parseInt(lineData[0]));
            epicTasks.put(epic.getId(), epic);
            return epic;
        } else if (lineData[1].equals("SUBTASK")) {
            Subtask subtask = new Subtask(lineData[2], lineData[4], status);
            subtask.setIdentityNumber(Integer.parseInt(lineData[0]));
            subtask.setEpicId(Integer.parseInt(lineData[5]));
            subTasks.put(subtask.getId(), subtask);
            epicTasks.get(subtask.getEpicId()).setSubtask(subtask);
            checkEpicStatus(epicTasks.get(subtask.getEpicId()));
            return subtask;
        }
        return null;
    }

    private static String historyToString(HistoryManager historyManager) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < historyManager.getHistory().size(); i++) {
            sb.append(historyManager.getHistory().get(i).getId());
            if (i < historyManager.getHistory().size() - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        String[] lineData = value.split(",");
        for (String temp : lineData) {
            try {
                result.add(Integer.parseInt(temp));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fbt = new FileBackedTasksManager(file);
        List<String> fileContent = new ArrayList<>();
        try (Reader fileReader = new FileReader(file);
             BufferedReader br = new BufferedReader(fileReader)) {
            while (br.ready()) {
                String line = br.readLine();
                fileContent.add(line);
            }
            for (int i = 0; i < fileContent.size() - 2; i++) {
                fbt.fromString(fileContent.get(i));
            }
            List<Integer> historyList = historyFromString(fileContent.get(fileContent.size() - 1));
            for (Integer id : historyList) {
                if (fbt.regularTasks.containsKey(id)) {
                    fbt.historyManager.add(fbt.regularTasks.get(id));
                } else if (fbt.epicTasks.containsKey(id)) {
                    fbt.historyManager.add(fbt.epicTasks.get(id));
                } else if (fbt.subTasks.containsKey(id)) {
                    fbt.historyManager.add(fbt.subTasks.get(id));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fbt;
    }

    @Override
    public void setIdentityNumber(int identityNumber) {
        super.setIdentityNumber(identityNumber);
        save();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return super.getHistoryManager();
    }

    @Override
    public void add(Task task) {
        super.add(task);
        save();
    }

    @Override
    public void add(Epic epic) {
        super.add(epic);
        save();
    }

    @Override
    public void add(Subtask subtask) {
        super.add(subtask);
        save();
    }

    @Override
    public void removeAllRegularTasks() {
        super.removeAllRegularTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        try {
            return super.getTask(id);
        } finally {
            save();
        }
    }

    @Override
    public Epic getEpic(int id) {
        try {
            return super.getEpic(id);
        } finally {
            save();
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        try {
            return super.getSubtask(id);
        } finally {
            save();
        }
    }

    @Override
    public void updateTaskById(Task task) {
        super.updateTaskById(task);
        save();
    }

    @Override
    public void updateEpicById(Epic epic) {
        super.updateEpicById(epic);
        save();
    }

    @Override
    public void updateSubtaskById(Subtask subtask) {
        super.updateSubtaskById(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void checkEpicStatus(Epic epic) {
        super.checkEpicStatus(epic);
        save();
    }
}
