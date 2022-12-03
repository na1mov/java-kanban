package model;

import java.util.Objects;

public class Task {
    protected final String name;
    protected final String details;
    private int identityNumber;
    protected TaskStatus status;
    protected TaskType type;

    public Task(String name, String details, TaskStatus status) {
        this.name = name;
        this.details = details;
        this.status = status;
        setType();
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

    public void setIdentityNumber(int identityNumber) {
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
        return "Task{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", status=" + status +
                '}';
    }
}