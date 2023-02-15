package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList customTasksHistory = new CustomLinkedList();

    @Override
    public List<Task> getHistory() {
        return customTasksHistory.getTasks();
    }

    @Override
    public void add(Task task) {
        customTasksHistory.linkLast(task);
    }

    @Override
    public void remove(int id) {
        customTasksHistory.removeNode(customTasksHistory.tasks.get(id));
    }

    public static class CustomLinkedList { //кастомный двусвязный список
        private final Map<Integer, Node> tasks = new HashMap<>();
        private Node head;
        private Node tail;

        public void linkLast(Task task) { // добавление задачи в конец списка
            Node node = new Node();
            node.setTask(task);

            if (tasks.containsKey(task.getId())) {
                removeNode(tasks.get(task.getId()));
            }

            if (head == null) {
                tail = node;
                head = node;
            } else {
                node.setPrevious(tail);
                tail.setNext(node);
                tail = node;
            }
            tasks.put(task.getId(), node);
        }

        public List<Task> getTasks() { //сбор всех задач в обычный список

            List<Task> tasks = new ArrayList<>();
            Node node = head;

            while (node != null) {
                    tasks.add(node.getTask());
                    node = node.getNext();
                }
            return tasks;
        }

        public void removeNode(Node node) { //метод на удаление узла

            if (node != null) {
                tasks.remove(node.getTask().getId());

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrevious();
                }
                if (node.getPrevious() != null) {
                    node.getPrevious().setNext(node.getNext());
                }
                if (node.getNext() != null) {
                    node.getNext().setPrevious(node.getPrevious());
                }
            }
        }
        }
    }