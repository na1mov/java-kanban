package Tests;

import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private static TaskManager manager;
    private static Epic testEpic;
    private static Subtask firstSubtask;
    private static Subtask secondSubtask;
    private static Subtask thirdSubtask;

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefault();
        testEpic = new Epic("Test Epic", "Test Epic Description", TaskStatus.NEW);
        manager.add(testEpic);
        firstSubtask = new Subtask("Test Subtask 1", "Test Subtask 1 Description",
                TaskStatus.NEW, testEpic.getId());
        secondSubtask = new Subtask("Test Subtask 2", "Test Subtask 2 Description",
                TaskStatus.NEW, testEpic.getId());
        thirdSubtask = new Subtask("Test Subtask 3", "Test Subtask 3 Description",
                TaskStatus.NEW, testEpic.getId());
        manager.add(firstSubtask);
        manager.add(secondSubtask);
        manager.add(thirdSubtask);
    }

    @Test
    public void shouldBeDoneWhenSubtaskListIsEmpty() {
        manager.removeAllSubtasks();
        assertEquals(TaskStatus.DONE, testEpic.getStatus());
    }

    @Test
    public void shouldBeNewWhenAllSubtasksIsNew() {
        assertEquals(TaskStatus.NEW, testEpic.getStatus());
    }

    @Test
    public void shouldBeDoneWhenAllSubtasksIsDone() {
        Subtask newFirstSubtask = new Subtask("Test Subtask 1", "Test Subtask 1 isDone",
                TaskStatus.DONE, testEpic.getId());
        Subtask newSecondSubtask = new Subtask("Test Subtask 2", "Test Subtask 2 isDone",
                TaskStatus.DONE, testEpic.getId());
        Subtask newThirdSubtask = new Subtask("Test Subtask 3", "Test Subtask 3 isDone",
                TaskStatus.DONE, testEpic.getId());
        newFirstSubtask.setId(firstSubtask.getId());
        newSecondSubtask.setId(secondSubtask.getId());
        newThirdSubtask.setId(thirdSubtask.getId());
        manager.updateSubtask(newFirstSubtask);
        manager.updateSubtask(newSecondSubtask);
        manager.updateSubtask(newThirdSubtask);
        assertEquals(TaskStatus.DONE, testEpic.getStatus());
    }

    @Test
    public void shouldBeInProgressWhenSubtasksIsMixedNewOrDone() {
        Subtask newSecondSubtask = new Subtask("Test Subtask 2", "Test Subtask 2 isDone",
                TaskStatus.DONE, testEpic.getId());
        newSecondSubtask.setId(secondSubtask.getId());
        manager.updateSubtask(newSecondSubtask);
        assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }

    @Test
    public void shouldBeInProgressWhenAllSubtasksIsInProgress() {
        Subtask newFirstSubtask = new Subtask("Test Subtask 1", "Test 1 inProgress",
                TaskStatus.IN_PROGRESS, testEpic.getId());
        Subtask newSecondSubtask = new Subtask("Test Subtask 2", "Test 2 isProgress",
                TaskStatus.IN_PROGRESS, testEpic.getId());
        Subtask newThirdSubtask = new Subtask("Test Subtask 3", "Test 3 isProgress",
                TaskStatus.IN_PROGRESS, testEpic.getId());
        newFirstSubtask.setId(firstSubtask.getId());
        newSecondSubtask.setId(secondSubtask.getId());
        newThirdSubtask.setId(thirdSubtask.getId());
        manager.updateSubtask(newFirstSubtask);
        manager.updateSubtask(newSecondSubtask);
        manager.updateSubtask(newThirdSubtask);
        assertEquals(TaskStatus.IN_PROGRESS, testEpic.getStatus());
    }
}