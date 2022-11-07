package model;

public class Node {
    private Node prev;
    private Task task;
    private Node next;

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task value) {
        this.task = value;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "Node{" +
                "task=" + task +
                '}';
    }
}
