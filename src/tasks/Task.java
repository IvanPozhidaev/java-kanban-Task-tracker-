package tasks;

import enums.Status;
import enums.TaskType;
import java.util.Objects;
import java.time.Instant;

public class Task {
    protected int id; //идентификационный номер задачи
    protected String taskName; // имя задачи
    protected String taskDescription; //описание задачи
    protected Status taskStatus; //статус задачи
    protected TaskType type;
    private Instant startTime;
    private long duration;

    public Task(String taskName, String taskDescription, Status taskStatus, Instant startTime, long duration) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    //60L - количество секунд в минуте, думаю, нет смысла выносить это в константу
    public Instant getEndTime() {
        return startTime.plusSeconds(duration * 60L);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(taskName, task.taskName)
                && Objects.equals(taskDescription, task.taskDescription) && taskStatus == task.taskStatus
                && type == task.type && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, taskDescription, taskStatus, type, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                ", type=" + type +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
