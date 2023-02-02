package TaskModules;

import StatusModules.Status;
import StatusModules.TaskType;

import java.util.Objects;

public class Task {
    protected String taskName; // имя задачи
    protected String taskDescription; //описание задачи
    protected int id; //идентификационный номер задачи
    protected Status taskStatus; //статус задачи
    protected TaskType type;

    public Task(String taskName, String taskDescription, int id, Status taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.id = id;
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id == task.id && Objects.equals(taskName, task.taskName) &&
                Objects.equals(taskDescription, task.taskDescription) &&
                Objects.equals(taskStatus, task.taskStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, taskDescription, id, taskStatus);
    }

    @Override
    public String toString() {
        return "TaskModules.Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", id=" + id +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Status status) {
        this.taskStatus = status;
    }

    public TaskType getType() {
        return type;
    }
}
