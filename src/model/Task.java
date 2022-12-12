package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
    protected final String name;
    protected final String details;
    private int identityNumber;
    protected TaskStatus status;
    protected TaskType type;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    public Task(String name, String details, TaskStatus status) {
        this.name = name;
        this.details = details;
        this.status = status;
        setType();
    }

    public Task(String name, String details, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.details = details;
        this.status = status;
        setType();
        this.duration = duration;
        this.startTime = startTime;
        setEndTime();
    }

    public LocalDateTime getEndTime() {
        setEndTime();
        return endTime;
    }

    protected void setEndTime() {
        if (duration != null && startTime != null) {
            endTime = startTime.plus(duration);
        } else {
            System.out.println("Ошибка. Не задан один или оба параметра приоритета задачи.");
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setId(int identityNumber) {
        this.identityNumber = identityNumber;
    }

    public int getId() {
        return identityNumber;
    }

    public TaskType getType() {
        return type;
    }

    protected void setType() {
        this.type = TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return identityNumber == task.identityNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identityNumber);
    }

    @Override
    public String toString() {
        if (duration != null && startTime != null) {
            this.setEndTime();
        }
        return "Task{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                (startTime == null ? "" : ", start time=" + startTime.format(formatter)) +
                (duration == null ? "" : ", duration=" + duration.toMinutes() + "min") +
                (endTime == null ? "" : ", end time=" + this.getEndTime().format(formatter)) +
                '}';
    }
}