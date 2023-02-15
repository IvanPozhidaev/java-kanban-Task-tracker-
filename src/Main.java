import managers.FileBackedTasksManager;
import managers.Managers;
import enums.Status;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
//        System.out.println("Поехали!");
//
//        FileBackedTasksManager manager = Managers.getDefaultFileManager();
//
//        Task task1 = new Task("New task1", "Description of task1", 0,
//                Status.NEW);
//        manager.createTask(task1);
//        Task task2 = new Task("New task2", "Description of task2", 0,
//                Status.NEW);
//        manager.createTask(task2);
//
//
//        EpicTask epicTask1 = new EpicTask("New Epic1", "New epic1 description", 0,
//                Status.NEW);
//        manager.createEpicTask(epicTask1);
//        Subtask subtask1 = new Subtask("New subtask1", "Description of subtask1", 0,
//                Status.NEW, epicTask1.getId());
//        manager.createSubTask(subtask1);
//
//        FileBackedTasksManager manager2 = Managers.getDefaultFileManager();
//        manager2.loadFromFile();


//        TaskManager taskManager = Managers.getDefault(Managers.getDefaultHistory());
//
//        System.out.println("\nСоздание двух простых задач для теста");
//        Task task1 = new Task("New task1", "Description of task1", 0,
//                Status.NEW);
//        int task1Id = taskManager.createTask(task1); // id1
//        Task task2 = new Task("New task2", "Description of task2", 0,
//                Status.NEW);
//        int task2Id = taskManager.createTask(task2); // id2
//        System.out.println(taskManager.getListTasks());
//
//        System.out.println("\nСоздание эпиков с подзадачами");
//        EpicTask epicTask1 = new EpicTask("New Epic1", "New epic1 description", 0,
//                Status.NEW);
//        int epic1Id = taskManager.createEpicTask(epicTask1); // id3
//        Subtask subtask1 = new Subtask("New subtask1", "Description of subtask1", 0,
//                Status.NEW, epicTask1.getId());
//        int subtask1Id = taskManager.createSubTask(subtask1); // id4
//        Subtask subtask2 = new Subtask("New subtask2", "Description of subtask2", 0,
//                Status.NEW, epicTask1.getId());
//        int subtask2Id = taskManager.createSubTask(subtask2); // id5
//
//        EpicTask epicTask2 = new EpicTask("New Epic2", "New epic2 description", 0,
//                Status.NEW);
//        int epic2Id = taskManager.createEpicTask(epicTask2); // id6
//        Subtask subtask3 = new Subtask("New subtask3", "Description of subtask3", 0,
//                Status.NEW, epicTask1.getId());
//        int subtask3Id = taskManager.createSubTask(subtask3); // id7
//
//        System.out.println("Тест по ТЗ5 - обновлённый функционал истории просмотров");
//
//
//        taskManager.getTaskById(task1Id);
//        System.out.println(taskManager.getHistory());
//        taskManager.getTaskById(task2Id);
//        System.out.println(taskManager.getHistory());
//        taskManager.getEpicTaskById(epic1Id);
//        System.out.println(taskManager.getHistory());
//        taskManager.getTaskById(1);
//        taskManager.getTaskById(2);
//        System.out.println(taskManager.getHistory());
//        taskManager.getEpicTaskById(epic2Id);
//        taskManager.getEpicTaskById(epic2Id);
//        taskManager.getSubtaskById(7);
//        taskManager.getSubtaskById(7);
//        taskManager.getSubtaskById(4);
//        taskManager.getSubtaskById(4);
//        taskManager.getSubtaskById(5);
//        taskManager.getSubtaskById(subtask1Id);
//        System.out.println(taskManager.getHistory());
//
//
//        taskManager.deleteTaskById(task1Id);
//        taskManager.deleteTaskById(task2Id);
//        System.out.println(taskManager.getHistory());
    }
}
