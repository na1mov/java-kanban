package service;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> taskHistory = new HashMap<>();
    private final Node first = new Node();
    private final Node last = new Node();


    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Ошибка ввода. Возможно задача была удалена.");
            return;
        }
        if (taskHistory.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
        taskHistory.put(task.getId(), last.getPrev());
    }

    @Override
    public List<Task> getHistory() {
        if (taskHistory == null) {
            System.out.println("Ошибка, история отсутствует");
            return null;
        }
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (taskHistory.containsKey(id)) {
            removeNode(taskHistory.get(id));
            taskHistory.remove(id);
        }
    }

    private void removeNode(Node node) {
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }

    /* Хотел создать отдельный класс для CustomLinkedList, но решил реализовать методы linkLast() и getTasks() в классе
     * InMemoryHistoryManager, т.к. по заданию было указано "Отдельный класс для списка создавать не нужно — реализуйте
     * его прямо в классе InMemoryHistoryManager." Возможно не так понял) */
    private void linkLast(Task task) {
        Node node = new Node();
        if (first.getNext() == null) {
            node.setTask(task);
            first.setNext(node);
            node.setPrev(first);
        }
        if (last.getPrev() == null) {
            last.setPrev(node);
            node.setNext(last);
            return;
        }

        node.setTask(task);
        Node lastNode = last.getPrev();
        lastNode.setNext(node);
        node.setPrev(lastNode);
        node.setNext(last);
        last.setPrev(node);
    }

    private List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();
        Node node = first.getNext();
        while (node != null && !node.equals(last)) {
            tasksList.add(node.getTask());
            node = node.getNext();
        }
        return tasksList;
    }
}
