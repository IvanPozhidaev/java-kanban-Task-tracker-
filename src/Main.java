import ManagerModules.Managers;
import ManagerModules.TaskManager;
import StatusModules.Status;
import TaskModules.EpicTask;
import TaskModules.Subtask;
import TaskModules.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        System.out.println("\nСоздание простой задачи");
        Task task1 = new Task("New task1", "Description of task1", 0,
                String.valueOf(Status.NEW));
        int task1Id = taskManager.createTask(task1);
        Task task2 = new Task("New task2", "Description of task2", 0,
                String.valueOf(Status.NEW));
        int task2Id = taskManager.createTask(task2);
        System.out.println(taskManager.getListTasks());

        System.out.println("\nСоздание эпиков с подзадачами");
        EpicTask epicTask1 = new EpicTask("New Epic1", "New epic1 description", 0,
                String.valueOf(Status.NEW));
        int epic1Id = taskManager.createEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("New subtask1", "Description of subtask1", 0,
                String.valueOf(Status.NEW), epicTask1.getId());
        int subtask1Id = taskManager.createSubTask(subtask1);
        Subtask subtask2 = new Subtask("New subtask2", "Description of subtask2", 0,
                String.valueOf(Status.NEW), epicTask1.getId());
        int subtask2Id = taskManager.createSubTask(subtask2);

        System.out.println("LIST OF EPIC TASKS");
        System.out.println(taskManager.getListEpicTasks());
        System.out.println("LIST OF SUBTASKS");
        System.out.println(taskManager.getListSubtasks());

        EpicTask epicTask2 = new EpicTask("New Epic2", "New epic2 description", 0,
                String.valueOf(Status.NEW));
        int epic2Id = taskManager.createEpicTask(epicTask2);
        Subtask subtask3 = new Subtask("New subtask3", "Description of subtask3", 0,
                String.valueOf(Status.NEW), epicTask2.getId());
        int subtask3Id = taskManager.createSubTask(subtask3);

        System.out.println("LIST OF EPIC TASKS");
        System.out.println(taskManager.getListEpicTasks());
        System.out.println("LIST OF SUBTASKS");
        System.out.println(taskManager.getListSubtasks());
        System.out.println("GET TASK BY SPECIFIC ID");
        System.out.println(taskManager.getTaskById(1));
        System.out.println("UPDATE TASK");
        Task taskUpdated = new Task("NEW_NAME_TASK1","NEW_DESCRIPTION_TASK1", 0,
                String.valueOf(Status.IN_PROGRESS));
        taskUpdated.setId(1);
        taskManager.updateTask(taskUpdated);

        System.out.println("UPDATE SUBTASK");
        taskManager.updateSubTask(new Subtask("UPDATED SUBTASK", "UPDATED DESCRIPTION OF SUBTASK",
                4, String.valueOf(Status.IN_PROGRESS), epicTask1.getId()));
        System.out.println(taskManager.getSubtaskById(4));

        taskManager.getTaskById(task1Id);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task2Id);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicTaskById(epic1Id);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtask1Id);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtask2Id);
        System.out.println(taskManager.getHistory());
        taskManager.getSubtaskById(subtask3Id);
        System.out.println(taskManager.getHistory());
        taskManager.getEpicTaskById(epic2Id);
    }
}
