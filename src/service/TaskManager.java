package service;

import model.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void add(Task task);

    void add(Epic epic);

    void add(Subtask subtask);

    ArrayList<Task> getAllRegularTasks();

    ArrayList<Task> getAllEpics();

    ArrayList<Task> getAllSubTasks();

    void removeAllRegularTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void removeTaskById(int id);

    ArrayList<Subtask> getEpicSubtasks(int id);

    void checkEpicStatus(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    boolean isTimeValidationOk(Task task);
}
