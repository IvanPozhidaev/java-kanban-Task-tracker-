package tests;

import enums.Status;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {

    protected T manager;
    public abstract T createManager() throws IOException, InterruptedException;

    @BeforeEach
    void getManager() throws IOException, InterruptedException {
        manager = createManager();
    }

    protected Task addTask() {
        return new Task ("Название","Описание", Status.NEW, Instant.now(), 0);
    }

    protected EpicTask addEpic() {
        return new EpicTask("Название", "Описание", Status.NEW, Instant.now(), 0);
    }

    protected Subtask addSubtask (EpicTask epicTask) {
        return new Subtask ("Название", "Описание", Status.NEW, epicTask.getId(), Instant.now(), 0);
    }

    @Test
    public void addTaskTest() {
        Task task = addTask();
        manager.createTask(task);
        List<Task> tasks = manager.getListTasks();

        assertNotNull(task.getTaskStatus());
        assertEquals(Status.NEW, task.getTaskStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void addEpicTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);
        List<EpicTask> epics = manager.getListEpicTasks();

        assertNotNull(epic.getTaskStatus());
        assertEquals(Status.NEW, epic.getTaskStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubTasksIds());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void addSubTaskTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.createSubTask(subtask);

        List<Subtask> subtasks = manager.getListSubtasks();

        assertNotNull(subtask.getTaskStatus());
        assertEquals(epic.getId(), subtask.getEpicTaskId());
        assertEquals(Status.NEW, subtask.getTaskStatus());
        assertEquals(List.of(subtask), subtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubTasksIds());
    }

    @Test
    public void taskNullTest() {
        Task task = manager.createTask(null);
        assertNull(task);
    }

    @Test
    public void epicNullTest() {
        EpicTask epicTask = manager.createEpicTask(null);
        assertNull(epicTask);
    }

    @Test
    public void subtaskNullTest() {
        Subtask subtask = manager.createSubTask(null);
        assertNull(subtask);
    }

    @Test
    public void taskStatusUpdateFromNewToInProgressTest() {
        Task task = addTask();
        manager.createTask(task);

        task.setTaskStatus(Status.IN_PROGRESS);
        manager.updateTask(task);

        assertEquals(Status.IN_PROGRESS, manager.getTaskById(task.getId()).getTaskStatus());
    }

    @Test
    public void epicTaskStatusUpdateFromNewToInProgressTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);
        epic.setTaskStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }

    @Test
    public void subtaskStatusUpdateFromNewToInProgressTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.createSubTask(subtask);

        subtask.setTaskStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subtask);

        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(subtask.getId()).getTaskStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }

    @Test
    public void taskStatusUpdateToDoneTest() {
        Task task = addTask();
        manager.createTask(task);

        task.setTaskStatus(Status.DONE);
        manager.updateTask(task);

        assertEquals(Status.DONE, manager.getTaskById(task.getId()).getTaskStatus());
    }

    @Test
    public void epicTaskStatusUpdateToDoneTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);
        epic.setTaskStatus(Status.DONE);

        assertEquals(Status.DONE, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }

    @Test
    public void subtaskStatusUpdateToDoneTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.createSubTask(subtask);

        subtask.setTaskStatus(Status.DONE);
        manager.updateSubTask(subtask);

        assertEquals(Status.DONE, manager.getSubtaskById(subtask.getId()).getTaskStatus());
        assertEquals(Status.DONE, manager.getEpicTaskById(epic.getId()).getTaskStatus());
    }

    @Test
    public void shouldNotUpdateTaskIfNullTest() {
        Task task = addTask();
        manager.createTask(task);
        manager.updateTask(null);

        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void shouldNotUpdateEpicTaskIfNullTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);
        manager.updateEpicTask(null);

        assertEquals(epic, manager.getEpicTaskById(epic.getId()));
    }

    @Test
    public void shouldNotUpdateSubtaskIfNullTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.createSubTask(subtask);
        manager.updateSubTask(null);

        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void deleteAllTasksTest() {
        Task task = addTask();
        manager.createTask(task);
        manager.deleteAllTasks();

        assertEquals(Collections.EMPTY_LIST, manager.getListTasks());
    }

    @Test
    public void deleteAllEpicTasksTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);
        manager.deleteAllEpics();

        assertEquals(Collections.EMPTY_LIST, manager.getListEpicTasks());
    }

    @Test
    public void deleteAllSubtasksTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.createSubTask(subtask);

        manager.deleteAllSubtasks();

        assertTrue(epic.getSubTasksIds().isEmpty());
        assertTrue(manager.getListSubtasks().isEmpty());
    }

    @Test
    public void deleteAllSubtasksOfOneEpicByEpicIdTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.deleteAllSubtasksByEpic(epic);

        assertTrue(epic.getSubTasksIds().isEmpty());
        assertTrue(manager.getListSubtasks().isEmpty());
    }

    @Test
    public void deleteTaskByIdTest() {
        Task task = addTask();
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        assertEquals(Collections.EMPTY_LIST, manager.getListTasks());
    }

    @Test
    public void deleteEpicTaskByIdTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);
        manager.deleteEpicTaskById(epic.getId());

        assertEquals(Collections.EMPTY_LIST, manager.getListEpicTasks());
    }

    @Test
    public void shouldNotDeleteTaskIfWrongIdUsedTest() {
        Task task = addTask();
        manager.createTask(task);
        manager.deleteTaskById(0);

        assertEquals(List.of(task), manager.getListTasks());
    }

    @Test
    public void shouldNotDeleteEpicTaskIfWrongIdUsedTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);
        manager.deleteEpicTaskById(999);

        assertEquals(List.of(epic), manager.getListEpicTasks());
    }

    @Test
    public void shouldNotDeleteSubtaskIfWrongIdUsedTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.createSubTask(subtask);
        manager.deleteSubtaskById(450);

        assertEquals(List.of(subtask), manager.getListSubtasks());
        assertEquals(List.of(subtask.getId()), manager.getEpicTaskById(epic.getId()).getSubTasksIds());
    }

    @Test
    public void shouldNothingHappensIfTaskHashMapIsEmptyTest() {
        manager.deleteAllTasks();
        manager.deleteTaskById(0);
        assertEquals(0, manager.getListTasks().size());
    }

    @Test
    public void shouldNothingHappensIfEpicTaskHashMapIsEmptyTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        manager.deleteAllEpics();
        manager.deleteEpicTaskById(6879);

        assertEquals(0, manager.getListEpicTasks().size());
    }

    @Test
    public void shouldNothingHappensIfSubtaskHashMapIsEmptyTest() {
        manager.deleteAllSubtasks();
        manager.deleteSubtaskById(456);
        assertEquals(0, manager.getListSubtasks().size());
    }

    @Test
    public void shouldReturnEmptyListIfSubTaskListOfIdGotByEpicIdIsEmptyTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        List<Subtask> subtasks = manager.getAllSubtasksByEpicId(epic.getId());

        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListOfTasksIfTasksIsAbsentTest() {
        assertTrue(manager.getListTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListOfEpicTasksIfEpicTasksIsAbsentTest() {
        assertTrue(manager.getListEpicTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListOfSubtasksIfSubtasksIsAbsentTest() {
        assertTrue(manager.getListSubtasks().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskDoesNotExistTest() {
        assertNull(manager.getTaskById(1));
    }

    @Test
    public void shouldReturnNullIfEpicTaskDoesNotExistTest() {
        assertNull(manager.getEpicTaskById(1));
    }

    @Test
    public void shouldReturnNullSubtaskDoesNotExistTest() {
        assertNull(manager.getSubtaskById(1));
    }

    @Test
    public void shouldReturnEmptyHistoryTest() {
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksDoesNotExistTest() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnTasksHistoryTest() {
        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        Subtask subtask = addSubtask(epic);
        manager.createSubTask(subtask);

        manager.getEpicTaskById(epic.getId());
        manager.getSubtaskById(subtask.getId());

        List<Task> list = manager.getHistory();

        assertEquals(2, list.size());
        assertTrue(list.contains(subtask));
        assertTrue(list.contains(epic));
    }
}
