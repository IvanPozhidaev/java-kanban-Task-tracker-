package managers;

import enums.*;
import tasks.*;
import java.util.*;
import java.time.Instant;

public class InMemoryTaskManager implements TaskManager {

    int id = 1;
    // делаю приватными и финальными, чтобы повысить безопасность и менять только состояние
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private HistoryManager historyManager;
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    public InMemoryTaskManager(){}
    //keygen
    public int idUniqueGenerator() {
        return ++id;
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    // Метод для обновления статуса эпика - переделан
    @Override
    public void updateEpicStatus(EpicTask epicTask) {
        List<Integer> subtasks = epicTask.getSubTasksIds();
        if (subtasks.isEmpty()) {
            epicTask.setTaskStatus(Status.NEW);
            return;
        }
        Status status = null;
        for (int id : subtasks) {
            Subtask subtask = subtaskMap.get(id);
            if (status == null) {
                status = subtask.getTaskStatus();
                continue;
            }
            if (status.equals(subtask.getTaskStatus())
                    && !status.equals(Status.IN_PROGRESS)) {
                continue;
            }
            epicTask.setTaskStatus(Status.IN_PROGRESS);
            return;
        }
        epicTask.setTaskStatus(status);
    }

    // методы на получение всех задач\эпиков\подзадач обновлены на более лаконичную запись
    @Override
    public List<Task> getListTasks() {
        if (taskMap.size() == 0) {
            System.out.println("Список задач пуст.");
            return Collections.emptyList();
        }
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Subtask> getListSubtasks() {
        if (subtaskMap.size() == 0) {
            System.out.println("Список подзадач пуст.");
            return Collections.emptyList();
        }
        return new ArrayList<>(subtaskMap.values());
    }

    @Override
    public List<EpicTask> getListEpicTasks() {
        if (epicTaskMap.size() == 0) {
            System.out.println("Список задач типа \"эпик\" пуст.");
            return Collections.emptyList();
        }
        return new ArrayList<>(epicTaskMap.values());
    }

    // методы на удаление всех задач\эпиков\подзадач
    @Override
    public void deleteAllTasks() {
        taskMap.clear();
        prioritizedTasks.clear();
    }

    //здесь стираю и подзадачи тоже, потому что по условию ТЗ субтаски не существуют без эпиков
    @Override
    public void deleteAllEpics() {
        subtaskMap.clear();
        epicTaskMap.clear();
    }

    //метод на удаление всех подзадач одного эпика
    public void deleteAllSubtasksByEpic(EpicTask epic) {
        if (epic != null) {
            for (int subtaskId : epic.getSubTasksIds()) {
                Subtask subtask = subtaskMap.get(subtaskId);
                prioritizedTasks.remove(subtask);
                subtaskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getSubTasksIds().clear();
        }
    }

    //здесь отдельный метод для удаления субзадач, потому что эпики могут существовать без подзадач
    @Override
    public void deleteAllSubtasks() {
        for (EpicTask epic : epicTaskMap.values()) {
            for (int subtaskId : epic.getSubTasksIds()) {
                Subtask subtask = subtaskMap.get(subtaskId);
                prioritizedTasks.remove(subtask);
                subtaskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getSubTasksIds().clear();
        }
    }

    // методы на получение задачи\эпика\подзадачи по идентификатору (обновлены с учётом ведения истории запросов)
    @Override
    public Task getTaskById(int id) {
        Task task = taskMap.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask epicTask = epicTaskMap.get(id);
        if (epicTask != null) {
            historyManager.add(epicTask);
        }
        return epicTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtaskMap.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getSubtaskByEpicId(int id) {
        EpicTask epicTask = epicTaskMap.get(id);
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        for (int subtask : epicTask.getSubTasksIds()) {
            listSubtask.add(subtaskMap.get(subtask));
        }
        return listSubtask;
    }

    // методы на удаление задачи\эпика\подзадачи по идентификатору
    @Override
    public void deleteTaskById(int id) {
        if (taskMap.containsKey(id)) {
            prioritizedTasks.removeIf(task -> task.getId() == id);
            historyManager.remove(id);
            taskMap.remove(id);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    public void updateEpicTime(EpicTask epic) {
        List<Subtask> subtasks = getAllSubtasksByEpicId(epic.getId());
        Instant startTime = subtasks.get(0).getStartTime();
        Instant endTime = subtasks.get(0).getEndTime();

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime().isBefore(startTime)) {
                startTime = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(endTime)) {
                endTime = subtask.getEndTime();
            }
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        long duration = (endTime.toEpochMilli() - startTime.toEpochMilli());
        epic.setDuration(duration);
    }

    //метод на удаление эпика по его идентификатору
    @Override
    public void deleteEpicTaskById(int id) {
        EpicTask epic = epicTaskMap.get(id);
        if (epic != null) {
            epic.getSubTasksIds().forEach(subtaskId -> {
                prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), subtaskId));
                subtaskMap.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            epicTaskMap.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задача типа \"эпик\" не найдена.");
        }
    }

    //метод на удаление подзадачи по идентификатору
    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtaskMap.get(id);
        if (subtask != null) {
            EpicTask epic = epicTaskMap.get(subtask.getEpicTaskId());
            epic.getSubTasksIds().remove((Integer) subtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            prioritizedTasks.remove(subtask);
            subtaskMap.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача не найдена.");
        }
    }

    //методы на создание задачи\эпика\подзадачи (переделаны с учётом обновлённой структуры - теперь все методы)
    //проверка на null была добавлена для прохождения тестов на null
    @Override
    public Task createTask(Task task) {
        if(task == null) {
            return null;
        }
        task.setId(idUniqueGenerator());
        addNewPrioritizedTask(task);
        taskMap.put(task.getId(), task);
        return task;
    }

    //метод создания эпика;  проверка на null была добавлена для прохождения тестов на null
    @Override
    public EpicTask createEpicTask(EpicTask epicTask) {
        if (epicTask == null) {
            return null;
        }
        epicTask.setId(idUniqueGenerator());
        epicTaskMap.put(epicTask.getId(), epicTask);
        return epicTask;
    }

    //метод создания подзадачи; проверка на null была добавлена для прохождения тестов на null
    @Override
    public Subtask createSubTask(Subtask subtask) {
        if (subtask ==null) {
            return null;
        }
        subtask.setId(idUniqueGenerator());
        EpicTask epic = epicTaskMap.get(subtask.getEpicTaskId());
        if (epic != null) {
            addNewPrioritizedTask(subtask);
            subtaskMap.put(subtask.getId(), subtask);
            epic.setSubTasksIds(subtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            return subtask;
        } else {
            System.out.println("Ошибка - эпик не найден.");
            return null;
        }
    }

    // метод на обновление задачи
    @Override
    public void updateTask(Task task) {
        if (task != null && taskMap.containsKey(task.getId())) {
            addNewPrioritizedTask(task);
            taskMap.put(task.getId(), task);
        } else {
            System.out.println("Задача не найдена.");
        }
    }

    // метод на обновление эпика
    @Override
    public void updateEpicTask(EpicTask epicTask) {
        if (epicTask != null && epicTaskMap.containsKey(epicTask.getId())) {
            epicTaskMap.put(epicTask.getId(), epicTask);
            updateEpicStatus(epicTask);
            updateEpicTime(epicTask);
        } else {
            System.out.println("Задача типа \"эпик\" не найдена.");
        }
    }

    // метод на обновление подзадачи
    @Override
    public void updateSubTask(Subtask subtask) {
        if (subtask != null && subtaskMap.containsKey(subtask.getId())) {
            EpicTask epic = epicTaskMap.get(subtask.getEpicTaskId());
            if (epicTaskMap.containsKey(epic.getId())) {
                addNewPrioritizedTask(subtask);
                subtaskMap.put(subtask.getId(), subtask);
                updateEpicStatus(epic);
                updateEpicTime(epic);
            } else {
                System.out.println("Подзадача не найдена.");
            }
        }
    }

    // метод на получение списка всех подзадач определённого эпика по его идентификатору
    @Override
    public List<Subtask> getAllSubtasksByEpicId(int id) {
        if (epicTaskMap.containsKey(id)) {
            List<Subtask> subtasksNew = new ArrayList<>();
            EpicTask epic = epicTaskMap.get(id);
            for (int i = 0; i < epic.getSubTasksIds().size(); i++) {
                subtasksNew.add(subtaskMap.get(epic.getSubTasksIds().get(i)));
            }
            return subtasksNew;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public String toString() {
        return "InMemoryTaskManager{" +
                ", historyManager=" + historyManager +
                '}';
    }

    public void addToHistory(int id) {
        if (epicTaskMap.containsKey(id)) {
            historyManager.add(epicTaskMap.get(id));
        } else if (subtaskMap.containsKey(id)) {
            historyManager.add(subtaskMap.get(id));
        } else if (taskMap.containsKey(id)) {
            historyManager.add(taskMap.get(id));
        }
    }

    private void addNewPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        taskPriorityValidation();
    }

    //метод на получение приоритизации задач (через стрим, потому что надо куда-то применить новую теорию :) )
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public boolean isTimeChecked(Task task) {
        List<Task> tasks = List.copyOf(prioritizedTasks);
        int sizeTimeNull = 0;

        if (tasks.size() > 0) {
            for (Task taskSave : tasks) {
                if (taskSave.getStartTime() != null && taskSave.getEndTime() != null) {
                    if (task.getStartTime().isBefore(taskSave.getStartTime())
                            && task.getEndTime().isBefore(taskSave.getStartTime())) {
                        return true;
                    } else if (task.getStartTime().isAfter(taskSave.getEndTime())
                            && task.getEndTime().isAfter(taskSave.getEndTime())) {
                        return true;
                    }
                } else {
                    sizeTimeNull++;
                }
            }
            return sizeTimeNull == tasks.size();
        } else {
            return true;
        }
    }

    //метод на проверку приоритета задачи
    private void taskPriorityValidation() {
        List<Task> tasks = getPrioritizedTasks();

        for (int i = 1; i < tasks.size(); i++) {
            Task task = tasks.get(i);

            boolean taskHasIntersections = isTimeChecked(task);

            if (taskHasIntersections) {
                System.out.println("ВНИМАНИЕ! Задача №" + task.getId() + " и №" + tasks.get(i - 1) + "пересекаются");
            }
        }
    }
}
