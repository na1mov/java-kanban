import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Subtask> epicSubTasks;

    public Epic(String name, String details, TaskStatus status) {
        super(name, details, status);
        epicSubTasks = new ArrayList<>();
    }

    private void checkStatus() {
        if (epicSubTasks == null) {
            this.status = TaskStatus.DONE;
            return;
        }

        boolean isNew = false;
        boolean isDone = false;

        for (Subtask sub : epicSubTasks) {
            if (!sub.status.equals(TaskStatus.NEW)) {
                isNew = true;
                break;
            }
        }
        for (Subtask sub : epicSubTasks) {
            if (!sub.status.equals(TaskStatus.DONE)) {
                isDone = true;
                break;
            }
        }

        if (!isDone) {
            this.status = TaskStatus.DONE;
        } else if (!isNew) {
            this.status = TaskStatus.NEW;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
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
        subtask.epicId = this.getId();
        checkStatus();
    }
}
