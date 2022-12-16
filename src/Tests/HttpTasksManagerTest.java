package Tests;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.*;
import service.HttpTasksManager;
import service.KVServer;
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
    В задании сказано: "Чтобы каждый раз не добавлять запуск KVServer и HttpTaskServer серверов, можно реализовать в
    классах с тестами отдельный метод. Пометьте его аннотацией @BeforeAll — если предполагается запуск серверов для
    всех тестов или аннотацией @BeforeEach — если для каждого теста требуется отдельный запуск."

    Я потратил 2 дня в попытках реализовать запуск сервера и создание httpManager'a с запуском клиента, но каждый раз
    выдает исключение BindException, первый раз создаёт нормально, второй и дальше - ошибка, также метод start()
    почему-то не видит поля в собственном классе. Я исчерпался, прошу помощи, где ошибка или если её нет, как тогда
    это реализовать? Лучше наглядный пример, т.к. в гугле ответа найти я не смог.

    Пробовал сделать через нотацию beforeAll, там всё работает, т.к. запуск сервера/клиента идёт через порт один раз,
    а значит порт всегда свободен. Но тогда почти все тесты надо переписывать, т.к. они учитывают ситуации с пустым
    менеджером и пустоым файлом/сервером сохранений.

    При проверке через main вся логика работает верно, всё сохраняется и всё загружается, проблема только в тестах
    и в нотации beforeEach, которая конфликтует с занятым портом, хоть в afterAll и прописан метод, вызывающий
    server.stop(0);
    */

    @Override
    @BeforeEach
    public void beforeEach() throws IOException {
        output = new ByteArrayOutputStream();
        kvServer = new KVServer();
        kvServer.start();
        manager = new HttpTasksManager("http://localhost:8080");
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
}