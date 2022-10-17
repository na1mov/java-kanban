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

    void removeAllSubTasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    void updateTaskById(Task task);

    void updateEpicById(Epic epic);

    void updateSubtaskById(Subtask subtask);

    void removeTaskById(int id);

    ArrayList<Subtask> getEpicSubtasks(int id);

    void checkEpicStatus(Epic epic);

    List<Task> getHistory();
}
