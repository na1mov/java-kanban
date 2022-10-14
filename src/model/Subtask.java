package model;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String details, TaskStatus status) {
        super(name, details, status);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + name + '\'' +
                ", details='" + details + '\'' +
                ", epicId=" + epicId +
                ", status=" + status +
                '}';
    }
}