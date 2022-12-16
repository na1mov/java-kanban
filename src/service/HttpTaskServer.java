package service;

import model.*;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();

    private static final TaskManager manager = new FileBackedTasksManager(
            "c:\\Users\\aleks\\dev\\java-kanban\\resources\\save");

    public static void main(String[] args) throws IOException {
        Task firstTask = new Task("Задача 1", "Some moves", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 11, 30));
        Task secondTask = new Task("Задача 2", "Some extra moves", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 12, 30));
        Epic firstEpic = new Epic("Эпик 1", "Эпик с 3 подзадачами", TaskStatus.NEW);
        Epic secondEpic = new Epic("Эпик 2", "Эпик без подзадач", TaskStatus.NEW);

        manager.add(firstTask);
        manager.add(secondTask);
        manager.add(firstEpic);
        manager.add(secondEpic);

        Subtask firstSubtask = new Subtask("Подзадача 1", "выполнить половину 1 эпика 1",
                TaskStatus.NEW, firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 13, 30));
        Subtask secondSubtask = new Subtask("Подзадача 2", "выполнить половину 2 эпика 1",
                TaskStatus.NEW, firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 14, 30));
        Subtask thirdSubtask = new Subtask("Подзадача 3", "выполнить половину 3 эпика 1",
                TaskStatus.NEW, firstEpic.getId(), Duration.ofMinutes(10),
                LocalDateTime.of(2022, 12, 9, 15, 30));

        manager.add(firstSubtask);
        manager.add(secondSubtask);
        manager.add(thirdSubtask);

        Task thirdTask = new Task("Task 3", "Task 3 with null startTime", TaskStatus.NEW);
        manager.add(thirdTask);
        System.out.println("=".repeat(75));
        System.out.println("Печать списка задач с приоритетом по времени");
        System.out.println(manager.getPrioritizedTasks());
        System.out.println("=".repeat(75));

        System.out.println(manager.getAllRegularTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        System.out.println(manager.getEpicSubtasks(firstEpic.getId()));
        System.out.println("=".repeat(75));

        System.out.println("Печать вызовов задач(не история, возможны повторы):");
        System.out.println(manager.getEpic(firstEpic.getId()));
        System.out.println(manager.getTask(secondTask.getId()));
        System.out.println(manager.getEpic(secondEpic.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getTask(firstTask.getId()));
        System.out.println(manager.getSubtask(secondSubtask.getId()));
        System.out.println(manager.getSubtask(secondSubtask.getId()));
        System.out.println("=".repeat(75));

        HttpServer httpServer = HttpServer.create();

        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new HelloHandler());
        httpServer.start();

        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String method = httpExchange.getRequestMethod();
            String response = "";

            String path = httpExchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");

            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

            switch (method) {
                case "GET":
                    if (path.equals("/tasks")) {
                        response = gson.toJson(manager.getPrioritizedTasks());
                        httpExchange.sendResponseHeaders(200, 0);
                    } else if (splitPath.length > 2) {
                        switch (splitPath[2]) {
                            case "task":
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    response = gson.toJson(manager.getTask(Integer.parseInt(splitQuery[1])));
                                    httpExchange.sendResponseHeaders(200, 0);
                                } else {
                                    response = gson.toJson(manager.getAllRegularTasks());
                                    httpExchange.sendResponseHeaders(200, 0);
                                }
                                break;
                            case "subtask":
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    response = gson.toJson(manager.getSubtask(Integer.parseInt(splitQuery[1])));
                                    httpExchange.sendResponseHeaders(200, 0);
                                } else {
                                    response = gson.toJson(manager.getAllSubTasks());
                                    httpExchange.sendResponseHeaders(200, 0);
                                }
                                break;
                            case "epic":
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    response = gson.toJson(manager.getEpic(Integer.parseInt(splitQuery[1])));
                                    httpExchange.sendResponseHeaders(200, 0);
                                } else {
                                    response = gson.toJson(manager.getAllEpics());
                                    httpExchange.sendResponseHeaders(200, 0);
                                }
                                break;
                            case "history":
                                response = gson.toJson(manager.getHistory());
                                httpExchange.sendResponseHeaders(200, 0);
                                break;
                            default:
                                System.out.println("Запрос составлен неверно. " +
                                        "Тип задачи или история должно быть указано в пути: /save/{задача}");
                                httpExchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "POST":
                    if (splitPath.length > 2) {
                        switch (splitPath[2]) {
                            case "task":
                                Task task = gson.fromJson(body, Task.class);
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    task.setId(Integer.parseInt(splitQuery[1]));
                                    manager.updateTask(task);
                                } else {
                                    manager.add(task);
                                }
                                httpExchange.sendResponseHeaders(201, 0);
                                break;
                            case "subtask":
                                Subtask subtask = gson.fromJson(body, Subtask.class);
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    subtask.setId(Integer.parseInt(splitQuery[1]));
                                    manager.updateSubtask(subtask);
                                } else {
                                    manager.add(subtask);
                                }
                                httpExchange.sendResponseHeaders(201, 0);
                                break;
                            case "epic":
                                Epic epic = gson.fromJson(body, Epic.class);
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    epic.setId(Integer.parseInt(splitQuery[1]));
                                    manager.updateEpic(epic);
                                } else {
                                    manager.add(epic);
                                }
                                httpExchange.sendResponseHeaders(201, 0);
                                break;
                            default:
                                System.out.println("Тип задач(и) для изменения не указан или указан неверно. " +
                                        "Тип указывается в пути: /save/{задача}");
                                httpExchange.sendResponseHeaders(400, 0);
                        }
                    }
                    break;
                case "DELETE":
                    if (splitPath.length > 2) {
                        switch (splitPath[2]) {
                            case "task":
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    manager.removeTaskById(Integer.parseInt(splitQuery[1]));
                                } else {
                                    manager.removeAllRegularTasks();
                                }
                                httpExchange.sendResponseHeaders(200, 0);
                                break;
                            case "subtask":
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    manager.removeTaskById(Integer.parseInt(splitQuery[1]));
                                } else {
                                    manager.removeAllSubtasks();
                                }
                                httpExchange.sendResponseHeaders(200, 0);
                                break;
                            case "epic":
                                if (httpExchange.getRequestURI().getQuery() != null) {
                                    String[] splitQuery = httpExchange.getRequestURI().getQuery().split("=");
                                    manager.removeTaskById(Integer.parseInt(splitQuery[1]));
                                } else {
                                    manager.removeAllEpics();
                                }
                                httpExchange.sendResponseHeaders(200, 0);
                                break;
                            default:
                                System.out.println("Тип задач(и) для изменения не указан или указан неверно. " +
                                        "Тип указывается в пути: /save/{задача}");
                                httpExchange.sendResponseHeaders(400, 0);
                        }
                    }
            }

            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}
