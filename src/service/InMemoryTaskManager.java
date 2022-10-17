package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private static int identityNumber = 1;
    private final HashMap<Integer, Task> regularTasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, Subtask> subTasks;

    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        regularTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public int getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(int identityNumber) {
        this.identityNumber = identityNumber;
    }

    public HashMap<Integer, Task> getRegularTasks() {
        return regularTasks;
    }

    public HashMap<Integer, Epic> getEpicTasks() {
        return epicTasks;
    }

    public HashMap<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void add(Task task) {
        if (task == null || !task.getClass().getSimpleName().equals("Task")) {
            System.out.println("Ошибка ввода задачи");
        } else {
            setId(task);
            regularTasks.put(task.getId(), task);
        }
    }

    @Override
    public void add(Epic epic) {
        if (epic == null) {
            System.out.println("Ошибка ввода задачи");
        } else {
            setId(epic);
            epicTasks.put(epic.getId(), epic);
        }
    }

    @Override
    public void add(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Ошибка ввода подзадачи");
            return;
        }
        if (epicTasks.get(subtask.getEpicId()) == null) {
            System.out.println("Ошибка ввода эпика");
            return;
        }
        setId(subtask);
        subTasks.put(subtask.getId(), subtask);
        epicTasks.get(subtask.getEpicId()).setSubtask(subtask);
        checkEpicStatus(epicTasks.get(subtask.getEpicId()));
    }

    @Override
    public ArrayList<Task> getAllRegularTasks() {
        if (regularTasks == null) {
            System.out.println("Все задачи выполнены!)");
            return null;
        }
        return new ArrayList<>(regularTasks.values());
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        if (epicTasks == null) {
            System.out.println("Все эпики и их подзадачи выполнены!)");
            return null;
        }
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Task> getAllSubTasks() {
        if (subTasks == null) {
            System.out.println("Все подзадачи выполнены!)");
            return null;
        }
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllRegularTasks() {
        if (regularTasks != null) {
            regularTasks.clear();
        }
    }

    @Override
    public void removeAllEpics() {
        if (epicTasks != null) {
            epicTasks.clear();
        }
        removeAllSubTasks();
    }

    @Override
    public void removeAllSubTasks() {
        if (subTasks != null) {
            subTasks.clear();
            for (Epic epic : epicTasks.values()) {
                epic.clearEpicSubTasks();
                checkEpicStatus(epic);
            }
        }

    }

    @Override
    public Task getTask(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        historyManager.add(regularTasks.get(id));
        return regularTasks.getOrDefault(id, null);
    }

    @Override
    public Epic getEpic(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        historyManager.add(epicTasks.get(id));
        return epicTasks.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        historyManager.add(subTasks.get(id));
        return subTasks.getOrDefault(id, null);
    }

    private void setId(Task task) {
        task.setIdentityNumber(identityNumber);
        identityNumber++;
    }

    @Override
    public void updateTaskById(Task task) {
        if (task.getId() > this.identityNumber || task.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (regularTasks.containsKey(task.getId())) {
            regularTasks.remove(task.getId());
            regularTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpicById(Epic epic) {
        if (epic.getId() > this.identityNumber || epic.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (epicTasks.containsKey(epic.getId())) {
            epicTasks.remove(epic.getId());
            epicTasks.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtaskById(Subtask subtask) {
        if (subtask.getId() > this.identityNumber || subtask.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (subTasks.containsKey(subtask.getId())) {
            int tempEpicId = subTasks.get(subtask.getId()).getEpicId();
            subTasks.remove(subtask.getId());
            subtask.setEpicId(tempEpicId);
            epicTasks.get(tempEpicId).setSubtask(subtask);
            checkEpicStatus(epicTasks.get(tempEpicId));
            subTasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (regularTasks.containsKey(id)) {
            regularTasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            for (Subtask subtask : subTasks.values()) {
                if (subtask.getEpicId() == id) {
                    removeTaskById(subtask.getId());
                }
            }
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            epicTasks.get(subTasks.get(id).getEpicId()).removeSubtask(subTasks.get(id));
            checkEpicStatus(epicTasks.get(subTasks.get(id).getEpicId()));
            subTasks.remove(id);
        } else {
            System.out.println("Задача уже была удалена ранее");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        if (!epicTasks.containsKey(id)) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        return epicTasks.get(id).getEpicSubTasks();
    }

    @Override
    public void checkEpicStatus(Epic epic) {
        if (epic.getEpicSubTasks() == null) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        boolean isNew = false;
        boolean isDone = false;

        for (Subtask sub : epic.getEpicSubTasks()) {
            if (!sub.getStatus().equals(TaskStatus.NEW)) {
                isNew = true;
                break;
            }
        }
        for (Subtask sub : epic.getEpicSubTasks()) {
            if (!sub.getStatus().equals(TaskStatus.DONE)) {
                isDone = true;
                break;
            }
        }

        if (!isDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (!isNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}