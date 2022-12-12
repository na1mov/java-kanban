package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String details, TaskStatus status, int epicId) {
        super(name, details, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String details, TaskStatus status, int epicId,
                   Duration duration, LocalDateTime startTime) {
        super(name, details, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        if (duration != null && startTime != null) {
            this.setEndTime();
        }
        return "Subtask{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", epicId=" + epicId +
                ", status=" + status +
                (startTime == null ? "" : ", start time=" + startTime.format(formatter)) +
                (duration == null ? "" : ", duration=" + duration.toMinutes() + "min") +
                (endTime == null ? "" : ", end time=" + this.getEndTime().format(formatter)) +
                '}';
    }

    @Override
    protected void setType() {
        this.type = TaskType.SUBTASK;
    }
}