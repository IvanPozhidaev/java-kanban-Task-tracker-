package TaskModules;

import java.util.Objects;

public class Subtask extends Task {
    int epicTaskId;

    public Subtask(String taskName, String taskDescription, int id, String taskStatus, int epicTaskId) {
        super(taskName, taskDescription, id, taskStatus);
        this.epicTaskId = epicTaskId;
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        Subtask subTask = (Subtask) obj;
        return epicTaskId == subTask.epicTaskId;
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), epicTaskId);
    }

    public String toString() {
        return "SubTask{" +
                "epicTaskId=" + epicTaskId +
                ", taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", id=" + id +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
