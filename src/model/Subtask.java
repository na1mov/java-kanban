package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String details, TaskStatus status) {
        super(name, details, status);
    }

    public Subtask(String name, String details, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, details, status, duration, startTime);
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
            return "Subtask{" +
                    "name='" + name + '\'' +
                    ", details='" + details + '\'' +
                    ", epicId=" + epicId + '\'' +
                    ", status=" + status + '\'' +
                    ", start time=" + startTime.format(formatter) + '\'' +
                    ", duration=" + duration.toMinutes() + "min" + '\'' +
                    ", end time=" + this.getEndTime().format(formatter) +
                    '}';
        } else {
            return "Subtask{" +
                    "name='" + name + '\'' +
                    ", details='" + details + '\'' +
                    ", epicId=" + epicId + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

    @Override
    protected void setType() {
        this.type = TaskType.SUBTASK;
    }
}