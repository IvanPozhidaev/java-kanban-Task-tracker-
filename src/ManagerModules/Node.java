package ManagerModules;

import TaskModules.Task;

public class Node { //Класс "узла" делаю по примеру из кода теории
    private Task task; //поля приватные для большей безопасности, ниже для каждого есть геттеры и сеттеры
    private Node previous;
    private Node next;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
