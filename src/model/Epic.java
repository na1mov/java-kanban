package model;

import java.time.Duration;
import java.time.LocalDateTime;
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
    protected void setEndTime() {
        if (epicSubTasks != null) {
            for (Subtask subtask : epicSubTasks) {
                if (endTime == null) {
                    endTime = subtask.getEndTime();
                } else if (endTime.isBefore(subtask.getEndTime())) {
                    endTime = subtask.getEndTime();
                }
            }
        }
    }

    @Override
    public void setDuration(Duration duration) {
        if (this.duration == null) {
            this.duration = duration;
        } else {
            this.duration = Duration.ofMinutes(0);
            for (Subtask subtask : epicSubTasks) {
                this.duration = this.duration.plus(subtask.getDuration());
            }
        }
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        if (this.startTime == null || this.startTime.isAfter(startTime)) {
            this.startTime = startTime;
        }
    }

    @Override
    public String toString() {
        if (duration != null && startTime != null) {
            this.setEndTime();
        }
        return "Epic{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", epicSubTasks=" + epicSubTasks +
                ", status=" + status +
                (startTime == null ? "" : ", start time=" + startTime.format(formatter)) +
                (duration == null ? "" : ", duration=" + duration.toMinutes() + "min") +
                (endTime == null ? "" : ", end time=" + this.getEndTime().format(formatter)) +
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
        if (subtask.getStartTime() != null) {
            setStartTime(subtask.getStartTime());
        }
        if (subtask.getDuration() != null) {
            setDuration(subtask.getDuration());
        }
    }

    public void removeSubtask(Subtask subtask) {
        epicSubTasks.remove(subtask);
    }
}