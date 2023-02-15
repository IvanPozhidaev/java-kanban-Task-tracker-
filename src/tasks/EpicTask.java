package tasks;

import java.util.ArrayList;
import enums.*;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class EpicTask extends Task {
    private final List<Integer> subTasksIds = new ArrayList<>();
    private Instant endTime;

    public EpicTask(String taskName, String taskDescription, Status taskStatus, Instant startTime, long duration) {
        super(taskName, taskDescription, taskStatus, startTime, duration);
        this.endTime = super.getEndTime();
    }

    public void addSubtask(int subtaskId) {
        this.subTasksIds.add(subtaskId);
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(int id) {
        subTasksIds.add(id);
    }

    @Override
    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EpicTask epicTask = (EpicTask) o;
        return Objects.equals(subTasksIds, epicTask.subTasksIds) && Objects.equals(endTime, epicTask.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksIds, endTime);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasksIds=" + subTasksIds +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                ", startTime='" + getStartTime().toEpochMilli() + '\'' +
                ", endTime='" + getEndTime().toEpochMilli() + '\'' +
                ", duration='" + getDuration() +
                '}';
    }
}
