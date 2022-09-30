import java.util.Objects;

public class Task {
    public String name;
    public String details;
    private int identityNumber;
    public TaskStatus status;

    public Task(String name, String details, TaskStatus status) {
        this.name = name;
        this.details = details;
        this.status = status;
    }

    public void setIdentityNumber(int identityNumber) {
        this.identityNumber = identityNumber;
    }

    public int getId() {
        return identityNumber;
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
