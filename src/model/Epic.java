package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> epicSubTasks;

    public Epic(String name, String details, TaskStatus status) {
        super(name, details, status);
        epicSubTasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getEpicSubTasks() {
        return epicSubTasks;
    }

    public void clearEpicSubTasks() {
        this.epicSubTasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", epicSubTasks=" + epicSubTasks +
                ", status=" + status +
                '}';
    }

    @Override
    protected void setType() {
        this.type = TaskType.EPIC;
    }

    public void setSubtask(Subtask subtask) {
        if (subtask == null || !subtask.getClass().getSimpleName().equals("Subtask")) {
            System.out.println("Ошибка");
            return;
        }
        if (epicSubTasks.contains(subtask)) {
            epicSubTasks.remove(subtask);
            epicSubTasks.add(subtask);
        } else {
            epicSubTasks.add(subtask);
        }
    }

    public void removeSubtask(Subtask subtask) {
        epicSubTasks.remove(subtask);
    }
}