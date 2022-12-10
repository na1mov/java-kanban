package Tests;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.*;
import service.FileBackedTasksManager;
import service.TaskManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;

class FileBackedTasksManagerTest extends InMemoryTaskManagerTest {
    protected static TaskManager backedManager;

    @Test
    public void shouldSaveAllTasksAndHistoryInTargetFile() {
        Epic testEpic = new Epic("Test epic", "Test epic description", TaskStatus.NEW);
        manager.add(testEpic);
        Subtask testSubtask1 = new Subtask("Test subtask 1", "Test description", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.of(2022, 12, 9, 11, 30));
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

    @Test
    public void shouldSaveAndBackupAllInfo() {
        backedManager = FileBackedTasksManager.loadFromFile(
                new File("c:\\Users\\aleks\\dev\\java-kanban\\resources\\saveForTests"));
        if (manager.getAllRegularTasks() == null || backedManager.getAllRegularTasks() == null) {
            Assertions.assertNull(manager.getAllRegularTasks());
            Assertions.assertNull(backedManager.getAllRegularTasks());
        } else {
            Assertions.assertArrayEquals(manager.getAllRegularTasks().toArray(), backedManager.getAllRegularTasks().toArray());
        }
        if (manager.getAllEpics() == null || backedManager.getAllEpics() == null) {
            Assertions.assertNull(manager.getAllEpics());
            Assertions.assertNull(backedManager.getAllEpics());
        } else {
            Assertions.assertArrayEquals(manager.getAllEpics().toArray(), backedManager.getAllEpics().toArray());
        }
        if (manager.getAllSubTasks() == null || backedManager.getAllSubTasks() == null) {
            Assertions.assertNull(manager.getAllSubTasks());
            Assertions.assertNull(backedManager.getAllSubTasks());
        } else {
            Assertions.assertArrayEquals(manager.getAllSubTasks().toArray(), backedManager.getAllSubTasks().toArray());
        }
        Assertions.assertArrayEquals(manager.getHistory().toArray(), backedManager.getHistory().toArray());
    }

    @Override
    @BeforeEach
    public void beforeEach() {
        output = new ByteArrayOutputStream();
        manager = new FileBackedTasksManager(
                new File("c:\\Users\\aleks\\dev\\java-kanban\\resources\\saveForTests"));
        System.setOut(new PrintStream(output));
    }

    @Override
    @AfterEach
    public void afterEach() {
        super.afterEach();
    }

    // тесты для метода add(Task task)
    @Override
    public void shouldPrintMessageIfTaskIsNull() {
        super.shouldPrintMessageIfTaskIsNull();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldPrintMessageIfTaskIsNotTask() {
        super.shouldPrintMessageIfTaskIsNotTask();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldPrintMessageIfTaskValidationFailed() {
        super.shouldPrintMessageIfTaskValidationFailed();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldSetIdAndAddTaskToHashMap() {
        super.shouldSetIdAndAddTaskToHashMap();
        shouldSaveAndBackupAllInfo();
    }

    // тесты для метода add(Epic epic)
    @Override
    public void shouldPrintMessageIfEpicIsNull() {
        super.shouldPrintMessageIfEpicIsNull();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldSetIdAndAddEpicToHashMap() {
        super.shouldSetIdAndAddEpicToHashMap();
        shouldSaveAndBackupAllInfo();
    }

    // тесты для метода add(Subtask subtask)
    @Override
    public void shouldPrintMessageIfSubtaskIsNull() {
        super.shouldPrintMessageIfSubtaskIsNull();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldPrintMessageIfSubtaskValidationFailed() {
        super.shouldPrintMessageIfSubtaskValidationFailed();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldPrintMessageIfEpicIdIsWrong() {
        super.shouldPrintMessageIfEpicIdIsWrong();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldSetIdAndAddSubtaskToHashMap() {
        super.shouldSetIdAndAddSubtaskToHashMap();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода removeAllRegularTasks()
    @Override
    public void shouldClearTasksHashMapIfItsNotNull() {
        super.shouldClearTasksHashMapIfItsNotNull();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода removeAllEpics()
    @Override
    public void shouldClearEpicsHashMapIfItsNotNull() {
        super.shouldClearEpicsHashMapIfItsNotNull();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода removeAllSubtasks()
    @Override
    public void shouldClearSubtasksHashMapIfItsNotNull() {
        super.shouldClearSubtasksHashMapIfItsNotNull();
        shouldSaveAndBackupAllInfo();
    }

    // тесты для метода getTask(int id)
    @Override
    public void shouldReturnTaskByIdAndAddToHistory() {
        super.shouldReturnTaskByIdAndAddToHistory();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReturnNullAndPrintMessageIfTaskIdOutOfBound() {
        super.shouldReturnNullAndPrintMessageIfTaskIdOutOfBound();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReturnNullAndPrintMessageIfTaskWasRemoved() {
        super.shouldReturnNullAndPrintMessageIfTaskWasRemoved();
        shouldSaveAndBackupAllInfo();
    }

    // тесты для метода getEpic(int id)
    @Override
    public void shouldReturnEpicByIdAndAddToHistory() {
        super.shouldReturnEpicByIdAndAddToHistory();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReturnNullAndPrintMessageIfEpicIdOutOfBound() {
        super.shouldReturnNullAndPrintMessageIfEpicIdOutOfBound();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReturnNullAndPrintMessageIfEpicWasRemoved() {
        super.shouldReturnNullAndPrintMessageIfEpicWasRemoved();
        shouldSaveAndBackupAllInfo();
    }

    // тесты для метода getSubtask(int id)
    @Override
    public void shouldReturnSubtaskByIdAndAddToHistory() {
        super.shouldReturnSubtaskByIdAndAddToHistory();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReturnNullAndPrintMessageIfSubtaskIdOutOfBound() {
        super.shouldReturnNullAndPrintMessageIfSubtaskIdOutOfBound();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReturnNullAndPrintMessageIfSubtaskWasRemoved() {
        super.shouldReturnNullAndPrintMessageIfSubtaskWasRemoved();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода updateTask(Task task)
    @Override
    public void shouldPrintMessageIfTaskIdOutOfBound() {
        super.shouldPrintMessageIfTaskIdOutOfBound();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReplaceTaskForUpdatedTask() {
        super.shouldReplaceTaskForUpdatedTask();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода updateEpic(Epic epic)
    @Override
    public void shouldPrintMessageIfEpicIdOutOfBound() {
        super.shouldPrintMessageIfEpicIdOutOfBound();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReplaceEpicForUpdatedEpic() {
        super.shouldReplaceEpicForUpdatedEpic();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода updateSubtask(Subtask subtask)
    @Override
    public void shouldPrintMessageIfSubtaskIdOutOfBound() {
        super.shouldPrintMessageIfSubtaskIdOutOfBound();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldReplaceSubtaskForUpdatedSubtask() {
        super.shouldReplaceSubtaskForUpdatedSubtask();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода removeTaskById(int id)
    @Override
    public void shouldPrintMessageIfIdOutOfBound() {
        super.shouldPrintMessageIfIdOutOfBound();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldPrintMessageIfTaskWasDeleted() {
        super.shouldPrintMessageIfTaskWasDeleted();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldRemoveTask() {
        super.shouldRemoveTask();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldRemoveEpic() {
        super.shouldRemoveEpic();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldRemoveSubtask() {
        super.shouldRemoveSubtask();
        shouldSaveAndBackupAllInfo();
    }

    // тест для метода checkEpicStatus(Epic epic)
    @Override
    public void shouldChangeEpicStatusToDoneIfSubtasksIsNullOrEmpty() {
        super.shouldChangeEpicStatusToDoneIfSubtasksIsNullOrEmpty();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldMakeEpicStatusNewIfAllSubtasksIsNew() {
        super.shouldMakeEpicStatusNewIfAllSubtasksIsNew();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldMakeEpicStatusDoneIfAllSubtasksIsDone() {
        super.shouldMakeEpicStatusDoneIfAllSubtasksIsDone();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldMakeEpicStatusInProgressIfSubtasksStatusIsMixed() {
        super.shouldMakeEpicStatusInProgressIfSubtasksStatusIsMixed();
        shouldSaveAndBackupAllInfo();
    }

    @Override
    public void shouldMakeEpicStatusInProgressIfSubtasksStatusIsInProgress() {
        super.shouldMakeEpicStatusInProgressIfSubtasksStatusIsInProgress();
        shouldSaveAndBackupAllInfo();
    }
}