package tasks;

import enums.*;

import java.time.Instant;
import java.util.Objects;

public class Subtask extends Task {
    private final int epicTaskId;

    public Subtask(String taskName, String taskDescription, Status taskStatus,
             int epicTaskId, Instant startTime, long duration) {
        super(taskName, taskDescription, taskStatus, startTime, duration);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicTaskId == subtask.epicTaskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicTaskId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicTaskId=" + epicTaskId +
                ", id=" + id +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                ", startTime='" + getStartTime().toEpochMilli() + '\'' +
                ", endTime='" + getEndTime().toEpochMilli() + '\'' +
                ", duration='" + getDuration() +
                '}';
    }
}
