package tests;

import managers.FileBackedTasksManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final Path path = Path.of("test_file.csv");
    File file = new File(String.valueOf(path));

    @Override
    public InMemoryTaskManager createManager() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        return manager;
    }

    @BeforeEach
    public void beforeEach() {
        manager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySaveAndLoad() {
        Task task = addTask();
        manager.createTask(task);

        EpicTask epic = addEpic();
        manager.createEpicTask(epic);

        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
        fileManager.loadFromFile();

        assertEquals(List.of(task), manager.getListTasks());
        assertEquals(List.of(epic), manager.getListEpicTasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);

        fileManager.save();
        fileManager.loadFromFile();

        assertEquals(Collections.EMPTY_LIST, manager.getListTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getListEpicTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getListSubtasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        FileBackedTasksManager fileManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);

        fileManager.save();
        fileManager.loadFromFile();

        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}