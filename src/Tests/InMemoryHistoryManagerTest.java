package Tests;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    protected static ByteArrayOutputStream output;
    protected static HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        output = new ByteArrayOutputStream();
        historyManager = Managers.getDefaultHistory();
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
        historyManager.add(task);
        Assertions.assertEquals("Ошибка ввода. Возможно задача была удалена.", output.toString().trim());
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task testTask = new Task("Test task", "Test task description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        testTask.setId(1);
        historyManager.add(testTask);
        Assertions.assertTrue(historyManager.getHistory().contains(testTask));
    }

    @Test
    public void shouldAddTaskInTheBottomOfHistoryAndReplaceDuplicates() {
        Task testTask = new Task("Test task", "Test task description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        testTask.setId(1);
        historyManager.add(testTask);
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        testEpic.setId(2);
        historyManager.add(testEpic);
        Subtask testSubtask = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        testSubtask.setId(3);
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        testSubtask2.setId(4);
        testSubtask.setEpicId(testEpic.getId());
        testSubtask2.setEpicId(testEpic.getId());
        historyManager.add(testSubtask);
        historyManager.add(testSubtask2);
        historyManager.add(testTask);
        int testPlacementOfTestTask = -1;
        int testPlacementOfTestSubtask2 = -1;
        for (int i = 0; i < historyManager.getHistory().size(); i++) {
            if (historyManager.getHistory().get(i).equals(testTask)) {
                testPlacementOfTestTask = i;
            } else if (historyManager.getHistory().get(i).equals(testSubtask2)) {
                testPlacementOfTestSubtask2 = i;
            }
        }
        Assertions.assertEquals(testPlacementOfTestTask, historyManager.getHistory().size() - 1);
        Assertions.assertEquals(testPlacementOfTestSubtask2, historyManager.getHistory().size() - 2);
    }

    // тесты для метода getHistory()
    @Test
    public void shouldReturnEmptyArrayListIfHistoryIsEmpty() {
        Assertions.assertArrayEquals(new ArrayList<>().toArray(), historyManager.getHistory().toArray());
    }

    @Test
    public void shouldReturnHistory() {
        Task testTask = new Task("Test task", "Test task description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        testTask.setId(1);
        historyManager.add(testTask);
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        testEpic.setId(2);
        historyManager.add(testEpic);
        Subtask testSubtask = new Subtask("Test subtask 1", "Test subtask description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
        testSubtask.setId(3);
        Subtask testSubtask2 = new Subtask("Test subtask 2", "Description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 12, 30));
        testSubtask2.setId(4);
        testSubtask.setEpicId(testEpic.getId());
        testSubtask2.setEpicId(testEpic.getId());
        historyManager.add(testSubtask);
        historyManager.add(testSubtask2);
        List<Task> testArray = new ArrayList<>();
        testArray.add(testTask);
        testArray.add(testEpic);
        testArray.add(testSubtask);
        testArray.add(testSubtask2);
        Assertions.assertArrayEquals(testArray.toArray(), historyManager.getHistory().toArray());
    }

    // тесты для метода remove(int id)
    @Test
    public void shouldRemoveTaskFromHistoryById() {
        Task testTask = new Task("Test task", "Test task description", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 10, 30));
        testTask.setId(1);
        historyManager.add(testTask);
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        testEpic.setId(2);
        historyManager.add(testEpic);
        historyManager.remove(1);
        Assertions.assertFalse(historyManager.getHistory().contains(testTask));
    }
}