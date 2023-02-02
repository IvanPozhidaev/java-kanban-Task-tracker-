package ManagerModules;

import TaskModules.*;
import StatusModules.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/*Привет, Степан! Мне наконец-то удалось написать что-то работающее по этому ТЗ.
* Метод loadFromFile по ТЗ надо было сделать статиком, но у меня получилось только void - надеюсь не критично
* Также сделал часть методов вспомогательных, для удобства
* Метод join в toString пришлось гуглить, потому что изначально у меня было 3 метода для каждого типа задачи
* Немного оптимизировал код в других классах (убрал лишние false, добавил\убрал проверки*/
public class FileBackedTasksManager extends InMemoryTaskManager {
    Path path = Path.of("src/data_file.csv");
    File file = new File(String.valueOf(path));

    public FileBackedTasksManager() {

    }

    //метод сохранения истории просмотров с проверкой наличия файла
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            String header = "id,type,name,status,description,epic" + "\n";
            writer.write(header);

            for(Task task : getListTasks()) {
                writer.write(toString(task) + "\n");
            }
            for(EpicTask epicTask : getListEpicTasks()) {
                writer.write(toString(epicTask) + "\n");
            }
            for(Subtask subtask : getListSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
            writer.write("\n");
            writer.write(historyToString(getHistoryManager()));
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка сохранения в файл.");
        }
    }

    //поскольку по ТЗ у нас есть поле epic, нам необходимо определять айди "родителя" эпика для субтасков
    //написал метод для этого
    private String getEpicId(Task task) {
        if (task instanceof Subtask) {
            return Integer.toString(((Subtask) task).getEpicTaskId());
        }
        return "";
    }

    //поскольку завели enum для типов задач, то потребовался метод для определения типа задачи,
    //который далее используется при преобразовании задачи в строку
    private TaskType getType(Task task) {
        if (task instanceof EpicTask) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        }
        return TaskType.TASK;
    }

    //метод сохранения задачи в строку
    private String toString(Task task) {
        String[] toJoin = {Integer.toString(task.getId()), getType(task).toString(), task.getTaskName(),
                task.getTaskStatus().toString(), task.getTaskDescription(), getEpicId(task)};
        return String.join(",", toJoin);
    }

    //сохранение менеджера истории
    static String historyToString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        StringBuilder string = new StringBuilder();

        if (history.isEmpty()) {
            return "";
        }
            for (Task task : history) {
                string.append(task.getId()).append(",");
            }

        return string.toString();
    }

    //восстанавливаем менеджер из истории файла CSV
    static List<Integer> historyFromString(String data) {
        List<Integer> list = new ArrayList<>();
        if (data != null) {
            String[] values = data.split(",");
            for (String str : values) {
                list.add(Integer.parseInt(str));
            }
            return list;
        }
        return list;
    }

    //создаём задачу из строки
    public static Task fromString(String data) {
        String[] option = data.split(",");
        if("EPIC".equals(option[1])) {
            EpicTask epicTask = new EpicTask(option[2], option[4], Integer.parseInt(option[0])
                    ,Status.valueOf(option[3])); //name, desc, id, status
            epicTask.setId(Integer.parseInt(option[0]));
            epicTask.setTaskStatus(Status.valueOf(option[3]));
            return epicTask;
        } else if ("SUBTASK".equals(option[1])) {
            Subtask subtask = new Subtask(option[2], option[4], Integer.parseInt(option[0]),
                    Status.valueOf(option[3]), Integer.parseInt(option[5]));
            subtask.setId(Integer.parseInt(option[0]));
            return subtask;
        } else {
            Task task = new Task(option[2], option[4], Integer.parseInt(option[0]),
                    Status.valueOf(option[3]));
            task.setId(Integer.parseInt(option[0]));
            return task;
        }
    }

    //восстанавливаем данные менеджера из файла при запуске программы
    public void loadFromFile() {
        try(BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            br.readLine();
            while(br.ready()) {
                String line = br.readLine();

                if(line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);

                if(task instanceof EpicTask epicTask) {
                    super.createEpicTask(epicTask);
                } else if (task instanceof Subtask subtask) {
                    super.createSubTask(subtask);
                } else {
                    super.createTask(task);
                }
            }
            String oneLineHistory = br.readLine();
            for(int id : historyFromString(oneLineHistory)) {
                addToHistory(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Возникла ошибка во время чтения файла.");
        }
    }

    //переопределение методов таск-менеджера с учётом нового класса и его нового функционала
    @Override
    public int createTask(Task task) {
        super.createTask(task);
        save();
        return task.getId();
    }

    @Override
    public int createEpicTask(EpicTask epicTask) {
        super.createTask(epicTask);
        save();
        return epicTask.getId();
    }

    @Override
    public int createSubTask(Subtask subtask) {
        super.createTask(subtask);
        save();
        return subtask.getId();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask epicTask = super.getEpicTaskById(id);
        save();
        return epicTask;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTask(EpicTask epicTask) {
        super.updateEpicTask(epicTask);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpicStatus(EpicTask epicTask) {
        super.updateEpicStatus(epicTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicTaskById(int id) {
        super.deleteEpicTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtaskByEpicId(int id) {
        ArrayList<Subtask> list = super.getSubtaskByEpicId(id);
        save();
        return list;
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
    public List<EpicTask> getListEpicTasks() {
        return super.getListEpicTasks();
    }

    public static void main(String[] args) {
        System.out.println("Поехали!");

        FileBackedTasksManager manager = Managers.getDefaultFileManager();

        Task task1 = new Task("New task1", "Description of task1", 0, Status.NEW);
        manager.createTask(task1);
        Task task2 = new Task("New task2", "Description of task2", 0, Status.NEW);
        manager.createTask(task2);

        EpicTask epicTask1 = new EpicTask("New Epic1", "New epic1 description", 0, Status.NEW);
        manager.createEpicTask(epicTask1);
        Subtask subtask1 = new Subtask("New subtask1", "Description of subtask1", 0, Status.NEW
                , epicTask1.getId());
        manager.createSubTask(subtask1);

        FileBackedTasksManager manager2 = Managers.getDefaultFileManager();
        manager2.loadFromFile();
    }
}
