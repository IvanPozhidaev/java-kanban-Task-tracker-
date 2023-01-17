package ManagerModules;

import TaskModules.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    /*Степан, привет! Комментарии в коде оставлены пока что, по большей части, для себя - пока обдумывал и
     * понимал, как должны работать методы. Если всё окажется верным - сотру простыни и оставлю лаконичные
     * Также хотел бы спросить, как можно более красиво и лаконично оформить методе removeNode - мне кажется, что
     * есть куда более элегантное решение, чем просто прописывать кучу if. */

    /*Убрал ограничение на количество записей в историю
     * переделал двусвязный список на кастомный двусвязный список
     * добавил хэш-таблицу, её также сделал финальной и приватной
     * все методы переписаны с нуля с учётом изменений в структуре данных и логике работы истории */

    /*из ТЗ было не очень понятно, надо ли было создавать мапу внутри кастомного списка или снаружи - сделал внутри,
     * т.к. из описания следовало, что надо связать их*/
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
            int taskID = task.getId();

            if (tasks.containsKey(task.getId())) {
                removeNode(tasks.get(taskID));
            }

            if (head == null) {
                tail = node;
                head = node;
                node.setNext(null);
                node.setPrevious(null);
            } else {
                node.setPrevious(tail);
                node.setNext(null);
                tail.setNext(node);
                tail = node;
            }
            tasks.put(taskID, node);
        }

        public List<Task> getTasks() { //сбор всех задач в обычный список
            /*согласно ТЗ надо получать историю, запихнв всё в список;
            * по ТЗ изначально мапа пустая, соответственно нам нужна ссылка на "голову", чтобы от неё начать заполнение;
             * заполнять лучше в цикле с условием? или в бесконечном цикле? - пока что сделал бесконечный цикл;
             * на возврат просто передаём список*/

            List<Task> tasks = new ArrayList<>();
            Node node = head;

            while (true) {
                if (node != null) {
                    tasks.add(node.getTask());
                    node = node.getNext();
                } else {
                    break;
                }
            }
            return tasks;
        }

        public void removeNode(Node node) {

            /* если я правильно понял, то чтобы удалить узел, надо просто переназначить ссылки в обход удаляемого узла
            * внутри зашил проверку на нулл, сделал просто условия if.*/

            if (node != null) {
                tasks.remove(node.getTask().getId());
                Node previous = node.getPrevious();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrevious();
                }
                if (previous != null) {
                    previous.setNext(next);
                }
                if (next != null) {
                    next.setPrevious(previous);
                }
            }
        }
        }
    }