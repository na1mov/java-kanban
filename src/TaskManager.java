import java.util.ArrayList;
import java.util.HashMap;

/* Судя по условию задачи большая часть проверок на null и на диапазон ID скорее всего будут не нужны,
 * т.к. пользователь будет контактировать непосредственно с интерфейсом программы, где отображаются актуальные задачи,
 * но на данном этапе прототипа решил их добавить, как заглушки, для большей наглядности :) */

public class TaskManager {
    private int identityNumber = 1;
    private HashMap<Integer, Task> regularTasks;
    private HashMap<Integer, Epic> epicTasks;
    private HashMap<Integer, Subtask> subTasks;

    public TaskManager() {
        regularTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public int getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(int identityNumber) {
        this.identityNumber = identityNumber;
    }

    public HashMap<Integer, Task> getRegularTasks() {
        return regularTasks;
    }

    public void setRegularTasks(HashMap<Integer, Task> regularTasks) {
        this.regularTasks = regularTasks;
    }

    public HashMap<Integer, Epic> getEpicTasks() {
        return epicTasks;
    }

    public void setEpicTasks(HashMap<Integer, Epic> epicTasks) {
        this.epicTasks = epicTasks;
    }

    public HashMap<Integer, Subtask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, Subtask> subTasks) {
        this.subTasks = subTasks;
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

    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Ошибка ввода подзадачи");
            return;
        }
        if (epicTasks.get(subtask.getEpicId()) == null) {
            System.out.println("Ошибка ввода эпика");
            return;
        }
        setId(subtask);
        subTasks.put(subtask.getId(), subtask);
        epicTasks.get(subtask.getEpicId()).setSubtask(subtask);
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
        removeAllSubTasks();
    }

    public void removeAllSubTasks() {
        if (subTasks != null) {
            subTasks.clear();
            for (Epic epic : epicTasks.values()) {
                epic.setEpicSubTasks(null);
                epic.checkStatus();
            }
        }

    }

    public Task getTaskById(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        return regularTasks.getOrDefault(id, null);
    }

    public Epic getEpicById(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        return epicTasks.getOrDefault(id, null);
    }

    public Subtask getSubtaskById(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        return subTasks.getOrDefault(id, null);
    }

    public void setId(Task task) {
        task.setIdentityNumber(identityNumber);
        identityNumber++;
    }

    public void updateTaskById(Task task) {
        if (task.getId() > this.identityNumber || task.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (regularTasks.containsKey(task.getId())) {
            regularTasks.remove(task.getId());
            regularTasks.put(task.getId(), task);
        }
    }

    public void updateEpicById(Epic epic) {
        if (epic.getId() > this.identityNumber || epic.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (epicTasks.containsKey(epic.getId())) {
            epicTasks.remove(epic.getId());
            epicTasks.put(epic.getId(), epic);
        }
    }

    public void updateSubtaskById(Subtask subtask) {
        if (subtask.getId() > this.identityNumber || subtask.getId() < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return;
        }
        if (subTasks.containsKey(subtask.getId())) {
            int tempEpicId = subTasks.get(subtask.getId()).getEpicId();
            subTasks.remove(subtask.getId());
            subtask.setEpicId(tempEpicId);
            epicTasks.get(tempEpicId).setSubtask(subtask);
            subTasks.put(subtask.getId(), subtask);
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
            for (Subtask subtask : subTasks.values()) {
                if (subtask.getEpicId() == id) {
                    removeTaskById(subtask.getId());
                }
            }
            epicTasks.remove(id);
        } else if (subTasks.containsKey(id)) {
            epicTasks.get(subTasks.get(id).getEpicId()).removeSubtask(subTasks.get(id));
            subTasks.remove(id);
        } else {
            System.out.println("Задача уже была удалена ранее");
        }
    }

    public ArrayList<Subtask> getEpicSubtasks(int id) {
        if (id > this.identityNumber || id < 1) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        if (!epicTasks.containsKey(id)) {
            System.out.println("Такой задачи ещё не было поставлено");
            return null;
        }
        return epicTasks.get(id).getEpicSubTasks();
    }
}