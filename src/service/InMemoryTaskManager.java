package service;

import model.*;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int identityNumber = 1;
    protected final HashMap<Integer, Task> regularTasks;
    protected final HashMap<Integer, Epic> epicTasks;
    protected final HashMap<Integer, Subtask> subTasks;
    protected Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if(task1.getStartTime() == null && task2.getStartTime() == null) {
            return 0;
        } else if (task1.getStartTime() == null) {
            return 1;
        } else if (task2.getStartTime() == null) {
            return -1;
        } else {
            return (int) Duration.between(task2.getStartTime(), task1.getStartTime()).toMinutes();
        }
    });
    protected final HistoryManager historyManager;

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
        } else if (!isTimeValidationOk(task)) {
            System.out.println("Ошибка. На это время запланирована другая задача.");
        } else {
            setId(task);
            regularTasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void add(Epic epic) {
        if (epic == null) {
            System.out.println("Ошибка ввода эпика");
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
        if (!isTimeValidationOk(subtask)) {
            System.out.println("Ошибка. На это время запланирована другая задача.");
            return;
        }
        if (epicTasks.getOrDefault(subtask.getEpicId(), null) == null) {
            System.out.println("Ошибка ввода ID эпика");
            return;
        }
        setId(subtask);
        subTasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        epicTasks.get(subtask.getEpicId()).setSubtask(subtask);
        checkEpicStatus(epicTasks.get(subtask.getEpicId()));
    }

    @Override
    public ArrayList<Task> getAllRegularTasks() {
        if (regularTasks == null || regularTasks.isEmpty()) {
            System.out.println("Все задачи выполнены!)");
            return null;
        }
        return new ArrayList<>(regularTasks.values());
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        if (epicTasks == null || epicTasks.isEmpty()) {
            System.out.println("Все эпики и их подзадачи выполнены!)");
            return null;
        }
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<Task> getAllSubTasks() {
        if (subTasks == null || subTasks.isEmpty()) {
            System.out.println("Все подзадачи выполнены!)");
            return null;
        }
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllRegularTasks() {
        if (regularTasks != null) {
            for (Integer i : regularTasks.keySet()) {
                historyManager.remove(i);
                prioritizedTasks.remove(regularTasks.get(i));
            }
            regularTasks.clear();
        }
    }

    @Override
    public void removeAllEpics() {
        if (epicTasks != null) {
            for (Integer i : epicTasks.keySet()) {
                historyManager.remove(i);
            }
            removeAllSubtasks();
            epicTasks.clear();
        }
    }

    @Override
    public void removeAllSubtasks() {
        if (subTasks != null) {
            for (Integer i : subTasks.keySet()) {
                historyManager.remove(i);
                prioritizedTasks.remove(subTasks.get(i));
            }
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
        if (!regularTasks.containsKey(id)) {
            System.out.println("Задача была удалена.");
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
        if (!epicTasks.containsKey(id)) {
            System.out.println("Эпик был удален.");
            return null;
        }
        historyManager.add(epicTasks.get(id));
        return epicTasks.getOrDefault(id, null);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой подзадачи ещё не было поставлено");
            return null;
        }
        if (!subTasks.containsKey(id)) {
            System.out.println("Подзадача была удалена.");
            return null;
        }
        historyManager.add(subTasks.get(id));
        return subTasks.getOrDefault(id, null);
    }

    protected void setId(Task task) {
        task.setId(identityNumber);
        identityNumber++;
    }

    @Override
    public void updateTask(Task task) {
        if (task.getId() > this.identityNumber || task.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }

        if (regularTasks.containsKey(task.getId())) {
            removeTaskById(task.getId());
            regularTasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic.getId() > this.identityNumber || epic.getId() < 1) {
            System.out.println("Такой эпик ещё не был задан");
            return;
        }
        if (epicTasks.containsKey(epic.getId())) {
            removeTaskById(epic.getId());
            epicTasks.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.getId() > this.identityNumber || subtask.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (subTasks.containsKey(subtask.getId())) {
            int tempEpicId = subTasks.get(subtask.getId()).getEpicId();
            removeTaskById(subtask.getId());
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
            prioritizedTasks.remove(regularTasks.get(id));
            regularTasks.remove(id);
            historyManager.remove(id);
        } else if (epicTasks.containsKey(id)) {
            int epicsSubtasksSize = epicTasks.get(id).getEpicSubTasks().size();
            for (int i = 0; i < epicsSubtasksSize; i++) {
                removeTaskById(epicTasks.get(id).getEpicSubTasks().get(0).getId());
            }
            epicTasks.remove(id);
            historyManager.remove(id);
        } else if (subTasks.containsKey(id)) {
            epicTasks.get(subTasks.get(id).getEpicId()).removeSubtask(subTasks.get(id));
            checkEpicStatus(epicTasks.get(subTasks.get(id).getEpicId()));
            prioritizedTasks.remove(subTasks.get(id));
            subTasks.remove(id);
            historyManager.remove(id);
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
            System.out.println("Эпик был удален");
            return null;
        }
        return epicTasks.get(id).getEpicSubTasks();
    }

    @Override
    public void checkEpicStatus(Epic epic) {
        if (epic.getEpicSubTasks() == null || epic.getEpicSubTasks().isEmpty()) {
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean isTimeValidationOk(Task task) {
        if(task.getStartTime() == null) {
            return true;
        }
        for(Task tempTask : getPrioritizedTasks()) {
            if(task.getId() != 0 && task.getId() == tempTask.getId()) {
                continue;
            }
            if(!(task.getStartTime().isBefore(tempTask.getStartTime())
                    && task.getEndTime().isBefore(tempTask.getStartTime()))
                    && !(task.getStartTime().isAfter(tempTask.getEndTime())
                    && task.getEndTime().isAfter(tempTask.getEndTime()))) {
                return false;
            }
        }
        return true;
    }
}