package Tests;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.*;
import service.HttpTasksManager;
import service.KVServer;
import service.Managers;
import service.TaskManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;

class HttpTasksManagerTest extends FileBackedTasksManagerTest {
    protected static TaskManager httpManager;
    protected static KVServer kvServer;

    /*
        Какая же это была простая ошибка, ведь нельзя вывести в консоль, если консоль = null, меня просто сбило, что
        выходило очень много ошибок связанных с портом и я пытался решить сначала этот вопрос, ну зато теперь навсегда
        запомню этот кейс xD
        Спасибо большое за помощь!:)
        Я в итоге заменил null на другой сис.аут и всё сработало, да ещё и часть тестов тоже сами исправились, т.к.
        консоль по факту обновляется ещё раз после создания сервера)
        Ещё вопрос, в пачку можно писать раз за спринт или, если я ни разу там не связывался, можно всё же пару раз?)
    */
    @Override
    @BeforeEach
    public void beforeEach() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault();
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @Override
    @AfterEach
    public void afterEach() {
        super.afterEach();
        kvServer.stop();
    }

    @Override
    @Test
    public void shouldSaveAllTasksAndHistoryInTargetFile() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test description", TaskStatus.IN_PROGRESS,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        testSubtask1.setEpicId(testEpic.getId());
        manager.add(testSubtask1);
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(firstTask);
        manager.getEpic(testEpic.getId());
        manager.getTask(firstTask.getId());
        manager.getSubtask(testSubtask1.getId());
        shouldSaveAndBackupAllInfo();
    }

    @Override
    @Test
    public void shouldSaveAndBackupAllInfo() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        manager.removeTaskById(testEpic.getId());
        httpManager = HttpTasksManager.loadFromUrl("http://localhost:8080");
        if (manager.getAllRegularTasks() == null || manager.getAllRegularTasks() == null) {
            Assertions.assertNull(manager.getAllRegularTasks());
            Assertions.assertNull(manager.getAllRegularTasks());
        } else {
            Assertions.assertArrayEquals(manager.getAllRegularTasks().toArray(),
                    manager.getAllRegularTasks().toArray());
        }
        if (manager.getAllEpics() == null || manager.getAllEpics() == null) {
            Assertions.assertNull(manager.getAllEpics());
            Assertions.assertNull(manager.getAllEpics());
        } else {
            Assertions.assertArrayEquals(manager.getAllEpics().toArray(), manager.getAllEpics().toArray());
        }
        if (manager.getAllSubTasks() == null || manager.getAllSubTasks() == null) {
            Assertions.assertNull(manager.getAllSubTasks());
            Assertions.assertNull(manager.getAllSubTasks());
        } else {
            Assertions.assertArrayEquals(manager.getAllSubTasks().toArray(), manager.getAllSubTasks().toArray());
        }
        Assertions.assertArrayEquals(manager.getHistory().toArray(), manager.getHistory().toArray());
    }

    @Override
    public void shouldReturnNullAndPrintMessageIfEpicWasDeleted() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        manager.add(firstEpic);
        manager.removeTaskById(firstEpic.getId());
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        Assertions.assertNull(manager.getEpicSubtasks(firstEpic.getId()));
    }
}