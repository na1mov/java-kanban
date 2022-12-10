package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
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

        Task firstTask = new Task("Задача 1", "Some moves", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Task secondTask = new Task("Задача 2", "Some extra moves", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        Epic firstEpic = new Epic("Эпик 1", "Эпик с 3 подзадачами", TaskStatus.NEW);
        Epic secondEpic = new Epic("Эпик 2", "Эпик без подзадач", TaskStatus.NEW);
        Subtask firstSubtask = new Subtask("Подзадача 1", "выполнить половину 1 эпика 1", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 13, 30));
        Subtask secondSubtask = new Subtask("Подзадача 2", "выполнить половину 2 эпика 1", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 14, 30));
        Subtask thirdSubtask = new Subtask("Подзадача 3", "выполнить половину 3 эпика 1", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 15, 30));

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
        System.out.println("Восстановленный ID счётчик:");
        System.out.println(secondManager.identityNumber);
        System.out.println("=".repeat(75));
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(filePath)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,epic\n");
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
            if (epic.getDuration() != null && epic.getStartTime() != null) {
                sb.append(epic.getStartTime());
                sb.append(',');
                sb.append(epic.getDuration().toMinutes());
                sb.append(',');
            }
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
            if (subtask.getDuration() != null && subtask.getStartTime() != null) {
                sb.append(subtask.getStartTime());
                sb.append(',');
                sb.append(subtask.getDuration().toMinutes());
                sb.append(',');
            }
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
            if (task.getDuration() != null && task.getStartTime() != null) {
                sb.append(task.getStartTime());
                sb.append(',');
                sb.append(task.getDuration().toMinutes());
                sb.append(',');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private Task fromString(String value) {
        String[] lineData = value.split(",");
        switch (lineData[1]) {
            case "TASK":
                Task task = new Task(lineData[2], lineData[4], TaskStatus.valueOf(lineData[3]));
                task.setId(Integer.parseInt(lineData[0]));
                if (lineData.length == 7) {
                    task.setStartTime(LocalDateTime.parse(lineData[5]));
                    task.setDuration(Duration.ofMinutes(Integer.parseInt(lineData[6])));
                }
                return task;
            case "EPIC":
                Epic epic = new Epic(lineData[2], lineData[4], TaskStatus.valueOf(lineData[3]));
                epic.setId(Integer.parseInt(lineData[0]));
                if (lineData.length == 7) {
                    epic.setStartTime(LocalDateTime.parse(lineData[5]));
                    epic.setDuration(Duration.ofMinutes(Integer.parseInt(lineData[6])));
                }
                return epic;
            case "SUBTASK":
                Subtask subtask = new Subtask(lineData[2], lineData[4], TaskStatus.valueOf(lineData[3]));
                subtask.setId(Integer.parseInt(lineData[0]));
                if (lineData.length == 8) {
                    subtask.setEpicId(Integer.parseInt(lineData[7]));
                    subtask.setStartTime(LocalDateTime.parse(lineData[5]));
                    subtask.setDuration(Duration.ofMinutes(Integer.parseInt(lineData[6])));
                } else {
                    subtask.setEpicId(Integer.parseInt(lineData[5]));
                }
                return subtask;
        }
        return null;
    }

    private static String historyToString(HistoryManager historyManager) {
        ArrayList<String> listHistoryID = new ArrayList<>();
        for (int i = 0; i < historyManager.getHistory().size(); i++) {
            listHistoryID.add("" + historyManager.getHistory().get(i).getId());
        }
        return String.join(",", listHistoryID);
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> result = new ArrayList<>();
        if (value.isEmpty()) {
            return result;
        }
        String[] lineData = value.split(",");
        for (String temp : lineData) {
            try {
                if (temp.matches("\\d+")) {
                    result.add(Integer.parseInt(temp));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fbt = new FileBackedTasksManager(file);
        List<String> fileContent = new ArrayList<>();
        int biggestSavedID = 0;
        try (Reader fileReader = new FileReader(file);
             BufferedReader br = new BufferedReader(fileReader)) {
            while (br.ready()) {
                String line = br.readLine();
                fileContent.add(line);
            }
            if (fileContent.size() < 3) {
                System.out.println("Файл сохранения пуст. Восстановление не возможно.");
            } else {
                for (int i = 0; i < fileContent.size() - 2; i++) {
                    Task task = fbt.fromString(fileContent.get(i));
                    if (task != null) {
                        if (task.getId() > biggestSavedID) {
                            biggestSavedID = task.getId();
                        }
                        switch (task.getType()) {
                            case TASK:
                                fbt.regularTasks.put(task.getId(), task);
                                break;
                            case EPIC:
                                fbt.epicTasks.put(task.getId(), (Epic) task);
                                break;
                            case SUBTASK:
                                Subtask subtask = (Subtask) task;
                                fbt.subTasks.put(task.getId(), (Subtask) task);
                                fbt.epicTasks.get(subtask.getEpicId()).setSubtask(subtask);
                                fbt.checkEpicStatus(fbt.epicTasks.get(subtask.getEpicId()));
                                break;
                        }
                    }
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fbt.identityNumber = biggestSavedID;
        return fbt;
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
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
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
