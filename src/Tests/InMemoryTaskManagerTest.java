package Tests;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    protected static ByteArrayOutputStream output;
    protected static TaskManager manager;

    @BeforeEach
    public void beforeEach() {
        output = new ByteArrayOutputStream();
        manager = Managers.getDefault();
        System.setOut(new PrintStream(output));

        /*
        думал добавить в этот метод инициализацию и добавление задач, но они нужны не для всех тестов и их набор
        также не нормирован, поэтому решил каждый тест писать отдельно
        */
    }

    @AfterEach
    public void afterEach() {
        System.setOut(null);
    }

    // тесты для метода add(Task task)
    @Test
    public void shouldPrintMessageIfTaskIsNull() {
        Task task = null;
        manager.add(task);
        Assertions.assertEquals("Ошибка ввода задачи", output.toString().trim());
    }

    @Test
    public void shouldPrintMessageIfTaskIsNotTask() {
        Task task = new Epic("Test Epic", "Test Epic Description", TaskStatus.NEW);
        manager.add(task);
        Assertions.assertEquals("Ошибка ввода задачи", output.toString().trim());
    }

    @Test
    public void shouldPrintMessageIfTaskValidationFailed() {
        Task testTask = new Task("Test task", "Test task description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        Task testTask2WithSameTime = new Task("Test task", "Test task description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testTask);
        manager.add(testTask2WithSameTime);
        Assertions.assertEquals("Ошибка. На это время запланирована другая задача.", output.toString().trim());
    }

    @Test
    public void shouldSetIdAndAddTaskToHashMap() {
        Task testTask = new Task("Test task", "Test task description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testTask);
        Assertions.assertTrue(testTask.getId() != 0);
        Assertions.assertTrue(manager.getAllRegularTasks().contains(testTask));
    }

    // тесты для метода add(Epic epic)
    @Test
    public void shouldPrintMessageIfEpicIsNull() {
        Epic epic = null;
        manager.add(epic);
        Assertions.assertEquals("Ошибка ввода эпика", output.toString().trim());
    }

    @Test
    public void shouldSetIdAndAddEpicToHashMap() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Assertions.assertTrue(testEpic.getId() != 0);
        Assertions.assertTrue(manager.getAllEpics().contains(testEpic));
    }

    // тесты для метода add(Subtask subtask)
    @Test
    public void shouldPrintMessageIfSubtaskIsNull() {
        Subtask subtask = null;
        manager.add(subtask);
        Assertions.assertEquals("Ошибка ввода подзадачи", output.toString().trim());
    }

    @Test
    public void shouldPrintMessageIfSubtaskValidationFailed() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask = new Subtask("Test subtask 1", "Test subtask description",
                TaskStatus.NEW, testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2WithSameTime = new Subtask("Test subtask 2", "Description",
                TaskStatus.NEW, testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask);
        manager.add(testSubtask2WithSameTime);
        Assertions.assertEquals("Ошибка. На это время запланирована другая задача.", output.toString().trim());
    }

    @Test
    public void shouldPrintMessageIfEpicIdIsWrong() {
        Subtask testSubtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW,
                42, Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask);
        Assertions.assertEquals("Ошибка ввода ID эпика", output.toString().trim());
    }

    @Test
    public void shouldSetIdAndAddSubtaskToHashMap() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask = new Subtask("Test subtask", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask);
        Assertions.assertTrue(testEpic.getId() != 0);
        Assertions.assertTrue(manager.getAllEpics().contains(testEpic));
    }

    // тесты для метода getAllRegularTasks()
    @Test
    public void shouldReturnNullAndPrintMessageIfTaskHashMapIsEmpty() {
        List<Task> result = manager.getAllRegularTasks();
        assertNull(result);
        Assertions.assertEquals("Все задачи выполнены!)", output.toString().trim());
    }

    @Test
    public void shouldReturnArrayListWithAllTasks() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        ArrayList<Task> testArray = new ArrayList<>();
        testArray.add(firstTask);
        testArray.add(secondTask);
        manager.add(firstTask);
        manager.add(secondTask);
        Assertions.assertArrayEquals(testArray.toArray(), manager.getAllRegularTasks().toArray());
    }

    // тесты для метода getAllEpics()
    @Test
    public void shouldReturnNullAndPrintMessageIfEpicHashMapIsEmpty() {
        List<Task> result = manager.getAllEpics();
        assertNull(result);
        Assertions.assertEquals("Все эпики и их подзадачи выполнены!)", output.toString().trim());
    }

    @Test
    public void shouldReturnArrayListWithAllEpics() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        Epic secondEpic = new Epic("Test epic 2", "Test epic 2 description", TaskStatus.NEW);
        ArrayList<Task> testArray = new ArrayList<>();
        testArray.add(firstEpic);
        testArray.add(secondEpic);
        manager.add(firstEpic);
        manager.add(secondEpic);
        Assertions.assertArrayEquals(testArray.toArray(), manager.getAllEpics().toArray());
    }

    // тесты для метода getAllSubtasks()
    @Test
    public void shouldReturnNullAndPrintMessageIfSubtaskHashMapIsEmpty() {
        List<Task> result = manager.getAllSubTasks();
        assertNull(result);
        Assertions.assertEquals("Все подзадачи выполнены!)", output.toString().trim());
    }

    @Test
    public void shouldReturnArrayListWithAllSubtasks() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        ArrayList<Task> testArray = new ArrayList<>();
        testArray.add(testSubtask1);
        testArray.add(testSubtask2);
        Assertions.assertArrayEquals(testArray.toArray(), manager.getAllSubTasks().toArray());
    }

    // тест для метода removeAllRegularTasks()
    @Test
    public void shouldClearTasksHashMapIfItsNotNull() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(firstTask);
        manager.add(secondTask);
        manager.removeAllRegularTasks();
        Assertions.assertNull(manager.getAllRegularTasks());
    }

    // тест для метода removeAllEpics()
    @Test
    public void shouldClearEpicsHashMapIfItsNotNull() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        Epic secondEpic = new Epic("Test epic 2", "Test epic 2 description", TaskStatus.NEW);
        manager.add(firstEpic);
        manager.add(secondEpic);
        manager.removeAllEpics();
        Assertions.assertNull(manager.getAllEpics());
    }

    // тест для метода removeAllSubtasks()
    @Test
    public void shouldClearSubtasksHashMapIfItsNotNull() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        manager.removeAllSubtasks();
        Assertions.assertNull(manager.getAllSubTasks());
    }

    // тесты для метода getTask(int id)
    @Test
    public void shouldReturnTaskByIdAndAddToHistory() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(firstTask);
        Assertions.assertEquals(firstTask, manager.getTask(firstTask.getId()));
        Assertions.assertEquals(firstTask, manager.getHistory().get(manager.getHistory().size() - 1));
    }

    @Test
    public void shouldReturnNullAndPrintMessageIfTaskIdOutOfBound() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(firstTask);
        Assertions.assertNull(manager.getTask(42));
        Assertions.assertEquals("Такой задачи ещё не было поставлено", output.toString().trim());
    }

    @Test
    public void shouldReturnNullAndPrintMessageIfTaskWasRemoved() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(firstTask);
        manager.removeTaskById(firstTask.getId());
        Assertions.assertNull(manager.getTask(firstTask.getId()));
        Assertions.assertEquals("Задача была удалена.", output.toString().trim());
    }

    // тесты для метода getEpic(int id)
    @Test
    public void shouldReturnEpicByIdAndAddToHistory() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        manager.add(firstEpic);
        Assertions.assertEquals(firstEpic, manager.getEpic(firstEpic.getId()));
        Assertions.assertEquals(firstEpic, manager.getHistory().get(manager.getHistory().size() - 1));
    }

    @Test
    public void shouldReturnNullAndPrintMessageIfEpicIdOutOfBound() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        manager.add(firstEpic);
        Assertions.assertNull(manager.getEpic(42));
        Assertions.assertEquals("Такой задачи ещё не было поставлено", output.toString().trim());
    }

    @Test
    public void shouldReturnNullAndPrintMessageIfEpicWasRemoved() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        manager.add(firstEpic);
        manager.removeTaskById(firstEpic.getId());
        Assertions.assertNull(manager.getEpic(firstEpic.getId()));
        Assertions.assertEquals("Эпик был удален.", output.toString().trim());
    }

    // тесты для метода getSubtask(int id)
    @Test
    public void shouldReturnSubtaskByIdAndAddToHistory() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask1);
        Assertions.assertEquals(testSubtask1, manager.getSubtask(testSubtask1.getId()));
        Assertions.assertEquals(testSubtask1, manager.getHistory().get(manager.getHistory().size() - 1));
    }

    @Test
    public void shouldReturnNullAndPrintMessageIfSubtaskIdOutOfBound() {
        Assertions.assertNull(manager.getSubtask(42));
        Assertions.assertEquals("Такой подзадачи ещё не было поставлено", output.toString().trim());
    }

    @Test
    public void shouldReturnNullAndPrintMessageIfSubtaskWasRemoved() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask1);
        manager.removeTaskById(testSubtask1.getId());
        Assertions.assertNull(manager.getSubtask(testSubtask1.getId()));
        Assertions.assertEquals("Подзадача была удалена.", output.toString().trim());
    }

    // тест для метода setId(Task task)
    @Test
    public void shouldReturnSetIdAndAddCount() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask1);
        Task testTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testTask);
        Assertions.assertEquals(1, testEpic.getId());
        Assertions.assertEquals(2, testSubtask1.getId());
        Assertions.assertEquals(3, testTask.getId());
    }

    // тест для метода updateTask(Task task)
    @Test
    public void shouldPrintMessageIfTaskIdOutOfBound() {
        Task testTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        testTask.setId(42);
        manager.updateTask(testTask);
        Assertions.assertEquals("Такой задачи ещё не было поставлено", output.toString().trim());
    }

    @Test
    public void shouldReplaceTaskForUpdatedTask() {
        Task testTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testTask);
        Task updTestTask = new Task("Test UpdTask 1", "Test task 1 updated", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        updTestTask.setId(testTask.getId());
        manager.updateTask(updTestTask);
        Assertions.assertEquals(updTestTask, manager.getTask(updTestTask.getId()));
    }

    // тест для метода updateEpic(Epic epic)
    @Test
    public void shouldPrintMessageIfEpicIdOutOfBound() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        testEpic.setId(42);
        manager.updateEpic(testEpic);
        Assertions.assertEquals("Такой эпик ещё не был задан", output.toString().trim());
    }

    @Test
    public void shouldReplaceEpicForUpdatedEpic() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Epic uptTestEpic = new Epic("Test updEpic 1", "Test updated epic 1 updated", TaskStatus.NEW);
        uptTestEpic.setId(testEpic.getId());
        manager.updateTask(uptTestEpic);
        Assertions.assertEquals(uptTestEpic, manager.getEpic(uptTestEpic.getId()));
    }

    // тест для метода updateSubtask(Subtask subtask)
    @Test
    public void shouldPrintMessageIfSubtaskIdOutOfBound() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        testEpic.setId(42);
        manager.updateEpic(testEpic);
        Assertions.assertEquals("Такой эпик ещё не был задан", output.toString().trim());
    }

    @Test
    public void shouldReplaceSubtaskForUpdatedSubtask() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask1);
        Subtask updTestSubtask1 = new Subtask("Test subtask upd", "Test subtask 1 updated", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        updTestSubtask1.setId(testSubtask1.getId());
        manager.updateSubtask(updTestSubtask1);
        Assertions.assertEquals(updTestSubtask1, manager.getSubtask(updTestSubtask1.getId()));
        Assertions.assertEquals(testEpic.getId(), manager.getSubtask(updTestSubtask1.getId()).getEpicId());
    }

    // тест для метода removeTaskById(int id)
    @Test
    public void shouldPrintMessageIfIdOutOfBound() {
        manager.removeTaskById(42);
        Assertions.assertEquals("Такой задачи ещё не было поставлено", output.toString().trim());
    }

    @Test
    public void shouldPrintMessageIfTaskWasDeleted() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        manager.add(firstEpic);
        manager.removeTaskById(firstEpic.getId());
        manager.removeTaskById(firstEpic.getId());
        Assertions.assertEquals("Задача уже была удалена ранее", output.toString().trim());
    }

    @Test
    public void shouldRemoveTask() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(firstTask);
        manager.add(secondTask);
        manager.removeTaskById(firstTask.getId());
        Assertions.assertFalse(manager.getAllRegularTasks().contains(firstTask));
        Assertions.assertFalse(manager.getPrioritizedTasks().contains(firstTask));
        Assertions.assertFalse(manager.getHistory().contains(firstTask));
    }

    @Test
    public void shouldRemoveEpic() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        Epic secondEpic = new Epic("Test epic 2", "Test epic 2 description", TaskStatus.NEW);
        manager.add(firstEpic);
        manager.add(secondEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        manager.removeTaskById(firstEpic.getId());
        Assertions.assertFalse(manager.getAllEpics().contains(firstEpic));
        Assertions.assertFalse(manager.getPrioritizedTasks().contains(firstEpic));
        Assertions.assertFalse(manager.getHistory().contains(firstEpic));
        assertNull(manager.getAllSubTasks());

    }

    @Test
    public void shouldRemoveSubtask() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        manager.removeTaskById(testSubtask1.getId());
        Assertions.assertFalse(manager.getAllSubTasks().contains(testSubtask1));
        Assertions.assertFalse(manager.getPrioritizedTasks().contains(testSubtask1));
        Assertions.assertFalse(manager.getHistory().contains(testSubtask1));
        Assertions.assertFalse(manager.getEpic(testEpic.getId()).getEpicSubTasks().contains(testSubtask1));
    }

    // тест для метода getEpicSubtasks(int id)
    @Test
    public void shouldReturnNullAndPrintMessageIfEpicsIdOutOfBound() {
        Assertions.assertNull(manager.getEpicSubtasks(42));
        Assertions.assertEquals("Такой задачи ещё не было поставлено", output.toString().trim());
    }

    @Test
    public void shouldReturnNullAndPrintMessageIfEpicWasDeleted() {
        Epic firstEpic = new Epic("Test epic 1", "Test epic 1 description", TaskStatus.NEW);
        manager.add(firstEpic);
        manager.removeTaskById(firstEpic.getId());
        Assertions.assertNull(manager.getEpicSubtasks(firstEpic.getId()));
        Assertions.assertEquals("Эпик был удален", output.toString().trim());
    }

    @Test
    public void shouldReturnEpicSubtasks() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        List<Subtask> testArray = new ArrayList<>();
        testArray.add(testSubtask1);
        testArray.add(testSubtask2);
        Assertions.assertArrayEquals(testArray.toArray(), manager.getEpicSubtasks(testEpic.getId()).toArray());
    }

    // тест для метода checkEpicStatus(Epic epic)
    @Test
    public void shouldChangeEpicStatusToDoneIfSubtasksIsNullOrEmpty() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        manager.checkEpicStatus(testEpic);
        Assertions.assertEquals(TaskStatus.DONE, testEpic.getStatus());
    }

    @Test
    public void shouldMakeEpicStatusNewIfAllSubtasksIsNew() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.DONE);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        manager.checkEpicStatus(testEpic);
        Assertions.assertEquals(TaskStatus.NEW, testEpic.getStatus());
    }

    @Test
    public void shouldMakeEpicStatusDoneIfAllSubtasksIsDone() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.DONE,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.DONE,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        manager.checkEpicStatus(testEpic);
        Assertions.assertEquals(TaskStatus.DONE, testEpic.getStatus());
    }

    @Test
    public void shouldMakeEpicStatusInProgressIfSubtasksStatusIsMixed() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.DONE,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        manager.checkEpicStatus(testEpic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }

    @Test
    public void shouldMakeEpicStatusInProgressIfSubtasksStatusIsInProgress() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test description", TaskStatus.IN_PROGRESS,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.IN_PROGRESS,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        manager.checkEpicStatus(testEpic);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }

    // тест для метода getHistory()
    @Test
    public void shouldReturnEmptyArrayListIfHistoryIsEmpty() {
        Assertions.assertArrayEquals(new ArrayList<>().toArray(), manager.getHistory().toArray());
    }

    @Test
    public void shouldReturnHistory() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test description", TaskStatus.IN_PROGRESS,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(testSubtask1);
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(firstTask);
        manager.getEpic(testEpic.getId());
        manager.getTask(firstTask.getId());
        manager.getSubtask(testSubtask1.getId());
        List<Task> testArray = new ArrayList<>();
        testArray.add(testEpic);
        testArray.add(firstTask);
        testArray.add(testSubtask1);
        Assertions.assertArrayEquals(testArray.toArray(), manager.getHistory().toArray());
    }

    // тест для метода getPrioritizedTasks()
    @Test
    public void shouldReturnEmptyArrayListIfThereIsNoTasks() {
        Assertions.assertArrayEquals(new ArrayList<>().toArray(), manager.getPrioritizedTasks().toArray());
    }

    @Test
    public void shouldReturnEmptyArrayListIfAllTasksWasRemoved() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        manager.add(firstTask);
        manager.removeTaskById(firstTask.getId());
        Assertions.assertArrayEquals(new ArrayList<>().toArray(), manager.getPrioritizedTasks().toArray());
    }

    @Test
    public void shouldReturnPrioritizedListOfTasks() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.DONE,
                testEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        manager.add(testSubtask1);
        manager.add(testSubtask2);
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        manager.add(firstTask);
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW);
        manager.add(secondTask);
        List<Task> testArray = new ArrayList<>();
        testArray.add(firstTask);
        testArray.add(testSubtask1);
        testArray.add(testSubtask2);
        testArray.add(secondTask);
        Assertions.assertArrayEquals(testArray.toArray(), manager.getPrioritizedTasks().toArray());
    }

    // тест для метода isTimeValidationOk(Task task)
    @Test
    public void shouldReturnTrueIfTasksStartTimeIsNull() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        manager.add(firstTask);
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW);
        Assertions.assertTrue(manager.isTimeValidationOk(secondTask));
    }

    @Test
    public void shouldReturnTrueIfTasksStartTimeIsFree() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        manager.add(firstTask);
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        Assertions.assertTrue(manager.isTimeValidationOk(secondTask));
    }

    @Test
    public void shouldReturnFalseIfTasksStartTimeIsNotFree() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        manager.add(firstTask);
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        Assertions.assertFalse(manager.isTimeValidationOk(secondTask));
    }

    @Test
    public void shouldReturnTrueIfTaskWithSameStartTimeAndSameId() {
        Task firstTask = new Task("Test task 1", "Test task 1 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        manager.add(firstTask);
        Task secondTask = new Task("Test task 2", "Test task 2 description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        secondTask.setId(firstTask.getId());
        Assertions.assertTrue(manager.isTimeValidationOk(secondTask));
    }
}