package service;

public class Managers {
    public static TaskManager getDefault() {
        return new HttpTasksManager("http://localhost:8080");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
