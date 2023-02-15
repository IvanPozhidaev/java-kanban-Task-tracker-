package tests;

import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryTaskManager(Managers.getDefaultHistory());
    }
}