import java.util.ArrayList;
import java.util.HashMap;

/* Судя по условию задачи большая часть проверок на null и на диапазон ID скорее всего будут не нужны,
 * т.к. пользователь будет контактировать непосредственно с интерфейсом программы, где отображаются актуальные задачи,
 * но на данном этапе прототипа решил их добавить, как заглушки, для большей наглядности :) */

public class TaskManager {
    public int identityNumber = 1;
    HashMap<Integer, Task> regularTasks;
    HashMap<Integer, Epic> epicTasks;
    HashMap<Integer, Subtask> subTasks;

    public TaskManager() {
        regularTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public void addTask(Task task) {
        if (task == null || !task.getClass().getSimpleName().equals("Task")) {
            // добавил проверку на принадлежность, чтоб случайно не внесли эпик или подзадачу
            System.out.println("Ошибка ввода задачи");
        } else {
            setId(task);
            regularTasks.put(task.getId(), task);
        }
    }

    public void addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Ошибка ввода задачи");
        } else {
            setId(epic);
            epicTasks.put(epic.getId(), epic);
        }
    }

    public void addSubtask(Epic epic, Subtask subtask) {
        if (subtask == null) {
            System.out.println("Ошибка ввода подзадачи");
            return;
        }
        if (epic == null) {
            System.out.println("Ошибка ввода эпика");
            return;
        }
        setId(subtask);
        subTasks.put(subtask.getId(), subtask);
        epic.setSubtask(subtask);
    }

    public ArrayList<Task> getAllRegularTasks() {
        if (regularTasks == null) {
            System.out.println("Все задачи выполнены!)");
            return null;
        }
        return new ArrayList<>(regularTasks.values());
    }

    public ArrayList<Task> getAllEpics() {
        if (epicTasks == null) {
            System.out.println("Все эпики и их подзадачи выполнены!)");
            return null;
        }
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<Task> getAllSubTasks() {
        if (subTasks == null) {
            System.out.println("Все подзадачи выполнены!)");
            return null;
        }
        return new ArrayList<>(subTasks.values());
    }

    public void removeAllRegularTasks() {
        if (regularTasks != null) {
            regularTasks.clear();
        }
    }

    public void removeAllEpics() {
        if (epicTasks != null) {
            epicTasks.clear();
        }
    }

    public void removeAllSubTasks() {
        if (subTasks != null) {
            subTasks.clear();
        }
    }

    public Task getTaskById(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        if (regularTasks.containsKey(id)) {
            return regularTasks.get(id);
        } else if (epicTasks.containsKey(id)) {
            return epicTasks.get(id);
        } else {
            return subTasks.get(id);
        }
    }

    public void setId(Task task) {
        task.setIdentityNumber(identityNumber);
        identityNumber++;
    }

    public void updateTaskById(int id, Task task) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (task == null) {
            System.out.println("Ошибка");
            return;
        }
        if (regularTasks.containsKey(id)) {
            regularTasks.remove(id);
            task.setIdentityNumber(id);
            regularTasks.put(id, task);
        }
        if (epicTasks.containsKey(id)) {
            epicTasks.remove(id);
            Epic epic = (Epic) task;
            epic.setIdentityNumber(id);
            epicTasks.put(id, epic);
        }
        if (subTasks.containsKey(id)) {
            int tempEpicId = subTasks.get(id).epicId;
            subTasks.remove(id);
            Subtask subtask = (Subtask) task;
            subtask.setIdentityNumber(id);
            epicTasks.get(tempEpicId).setSubtask(subtask);
            subTasks.put(id, subtask);
        }
    }

    public void removeTaskById(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (regularTasks.containsKey(id)) {
            regularTasks.remove(id);
        } else if (epicTasks.containsKey(id)) {
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            subTasks.remove(id);
        } else {
            System.out.println("Задача уже была удалена ранее");
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        if (epic == null) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        return epic.epicSubTasks;
    }
}