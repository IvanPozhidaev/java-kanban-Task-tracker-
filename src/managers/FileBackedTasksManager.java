package managers;

import tasks.*;
import enums.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;

public class FileBackedTasksManager extends InMemoryTaskManager {

    Path path = Path.of("src/tasks_file.csv");
    File file;

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }


    // метод сохранения в файл
    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,epic" + "\n");
            for (Task task : getListTasks()) {
                bufferedWriter.write(toString(task) + "\n");
            }
            for (EpicTask epic : getListEpicTasks()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getListSubtasks()) {
                bufferedWriter.write(toString(subtask) + "\n");
            }
            bufferedWriter.write("\n");
            bufferedWriter.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла");
        }
    }

    //метод на получение айди родительского эпика
    private String getEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicTaskId());
        }
        return "";
    }

    //метод на получение типа задачи
    private TaskType getType(Task task) {
        if (task instanceof EpicTask) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    // метод сохранения задачи в строку
    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(getType(task).toString()).append(",");
        sb.append(task.getTaskName()).append(",");
        sb.append(task.getTaskStatus().toString()).append(",");
        sb.append(task.getTaskDescription()).append(",");
        sb.append(task.getStartTime()).append(",");
        sb.append(task.getDuration()).append(",");
        sb.append(getEpicId(task)).append(",");
        return sb.toString();
    }
    // метод создания задачи из строки
    public static Task fromString(String data) {
        String[] option = data.split(",");
        if("EPIC".equals(option[1])) {
            EpicTask epicTask = new EpicTask(option[2], option[4], Status.valueOf(option[3]),
                    Instant.parse(option[5]), Long.parseLong(option[6])); //name, desc, status, startTime, duration
            epicTask.setId(Integer.parseInt(option[0]));
            epicTask.setTaskStatus(Status.valueOf(option[3]));
            return epicTask;
        } else if ("SUBTASK".equals(option[1])) {
            Subtask subtask = new Subtask(option[2], option[4], Status.valueOf(option[3]),
                    Integer.parseInt(option[7]), Instant.parse(option[5]), Long.parseLong(option[6]));
            subtask.setId(Integer.parseInt(option[0])); //name, desc, status, startTime, duration, epicId
            return subtask;
        } else {
            Task task = new Task(option[2], option[4], Status.valueOf(option[3]),
                    Instant.parse(option[5]), Long.parseLong(option[6])); //name, desc, status, startTime, duration
            task.setId(Integer.parseInt(option[0]));
            return task;
        }
    }
    // метод для сохранения истори
    static String historyToString(HistoryManager manager) {
        List<Task> history = manager.getHistory();
        StringBuilder str = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }

        for (Task task : history) {
            str.append(task.getId()).append(",");
        }

        if (str.length() != 0) {
            str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
    }

    // метод восстановления истории из строки
    static List<Integer> historyFromString(String value) {
        List<Integer> list = new ArrayList<>();
        if (value != null) {
            String[] val = value.split(",");
            for (String number : val) {
                list.add(Integer.parseInt(number));
            }
            return list;
        }
        return list;
    }

    // метод восстановления данных из файла
    public void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();

                if (line.isEmpty() || line.isBlank()) {
                    break;
                }
                Task task = fromString(line);

                if (task instanceof EpicTask epic) {
                    createEpicTask(epic);
                } else if (task instanceof Subtask subtask) {
                    createSubTask(subtask);
                } else {
                    createTask(task);
                }
            }
            String lineWithHistory = br.readLine();
            for (int id : historyFromString(lineWithHistory)) {
                addToHistory(id);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла!");
        }
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public EpicTask createEpicTask(EpicTask epic) {
        super.createEpicTask(epic);
        save();
        return epic;
    }

    @Override
    public Subtask createSubTask(Subtask subtask) {
        super.createSubTask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public List<EpicTask> getListEpicTasks() {
        return super.getListEpicTasks();
    }

    @Override
    public List<Task> getListTasks() {

        return super.getListTasks();
    }

    @Override
    public List<Subtask> getListSubtasks() {
        return super.getListSubtasks();

    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask epic = super.getEpicTaskById(id);
        save();
        return epic;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epic) {
        super.updateEpicTask(epic);
        save();
    }

    @Override
    public void updateEpicStatus(EpicTask epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasksByEpic(EpicTask epic) {
        super.deleteAllSubtasksByEpic(epic);
        save();
    }
}