package service;

import com.google.gson.Gson;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public class HttpTasksManager extends FileBackedTasksManager {
    private static final Gson gson = new Gson();
    private final KVTaskClient client;
    private String key = "taskManager";

    public HttpTasksManager(String url) {
        super(url);
        client = new KVTaskClient(url);
    }

    public KVTaskClient getClient() {
        return client;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static HttpTasksManager loadFromUrl(String file) {
        HttpTasksManager httpManager = new HttpTasksManager(file);
        KVTaskClient tempClient = new KVTaskClient(file);
        String jsonContent = tempClient.load("taskManager");
        String[] fileContent = gson.fromJson(jsonContent, String.class).split("\r?\n");
        int biggestSavedID = 0;

        if (fileContent.length < 3) {
            System.out.println("Файл сохранения пуст. Восстановление не возможно.");
        } else {
            for (int i = 0; i < fileContent.length - 2; i++) {
                Task task = httpManager.fromString(fileContent[i]);
                if (task != null) {
                    if (task.getId() > biggestSavedID) {
                        biggestSavedID = task.getId();
                    }
                    switch (task.getType()) {
                        case TASK:
                            httpManager.regularTasks.put(task.getId(), task);
                            break;
                        case EPIC:
                            httpManager.epicTasks.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            Subtask subtask = (Subtask) task;
                            httpManager.subTasks.put(task.getId(), (Subtask) task);
                            httpManager.epicTasks.get(subtask.getEpicId()).setSubtask(subtask);
                            httpManager.checkEpicStatus(httpManager.epicTasks.get(subtask.getEpicId()));
                            break;
                    }
                }
            }
            List<Integer> historyList = httpManager.historyFromString(fileContent[fileContent.length - 1]);
            for (Integer id : historyList) {
                if (httpManager.regularTasks.containsKey(id)) {
                    httpManager.historyManager.add(httpManager.regularTasks.get(id));
                } else if (httpManager.epicTasks.containsKey(id)) {
                    httpManager.historyManager.add(httpManager.epicTasks.get(id));
                } else if (httpManager.subTasks.containsKey(id)) {
                    httpManager.historyManager.add(httpManager.subTasks.get(id));
                }
            }
        }
        httpManager.identityNumber = biggestSavedID;
        return httpManager;
    }

    @Override
    protected void save() throws ManagerSaveException {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,startTime,duration,epic\n");
        for (int i = 1; i < identityNumber; i++) {
            if (regularTasks.containsKey(i)) {
                sb.append(toString(regularTasks.get(i)));
            } else if (epicTasks.containsKey(i)) {
                sb.append(toString(epicTasks.get(i)));
            } else if (subTasks.containsKey(i)) {
                sb.append(toString(subTasks.get(i)));
            }
        }
        sb.append(" \n");
        sb.append(historyToString(historyManager));
        client.put(key, gson.toJson(sb.toString()));
    }

    @Override
    protected String toString(Task task) {
        return super.toString(task);
    }

    @Override
    protected Task fromString(String value) {
        return super.fromString(value);
    }

    @Override
    protected String historyToString(HistoryManager historyManager) {
        return super.historyToString(historyManager);
    }

    @Override
    protected List<Integer> historyFromString(String value) {
        return super.historyFromString(value);
    }
}
