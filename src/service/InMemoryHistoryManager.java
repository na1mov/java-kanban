package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> taskHistory;

    public InMemoryHistoryManager() {
        taskHistory = new ArrayList<>();
    }

    public List<Task> getTaskHistory() {
        return taskHistory;
    }

    public void setTaskHistory(List<Task> taskHistory) {
        this.taskHistory = taskHistory;
    }

    @Override
    public void add(Task task) {
        taskHistory.add(task);
        if (taskHistory.size() > 10) {
            taskHistory.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        if (taskHistory == null) {
            System.out.println("Ошибка, история отсутствует");
            return null;
        }
        return taskHistory;
    }
}
